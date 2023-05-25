package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.Authority;
import com.strongcom.doormate.domain.Message;
import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.UserDto;
import com.strongcom.doormate.exception.DuplicateUserException;
import com.strongcom.doormate.exception.NotFoundUserException;
import com.strongcom.doormate.kakao.dto.KakaoGetUserDto;
import com.strongcom.doormate.kakao.service.KakaoService;
import com.strongcom.doormate.repository.UserRepository;
import com.strongcom.doormate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;

    @Transactional
    @Override
    public User createUser(UserDto userDto) {
        if(userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateUserException("이미 가입되어 있는 사용자 입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .targetToken(userDto.getTargetToken())
                .authorities(Collections.singleton(authority))
                .build();

        return userRepository.save(user);
    }

    public List<Reminder> findAll(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException("회원정보가 존재하지 않습니다."));
        return user.getReminders();
    }


    public User findByKakaoUser(HttpHeaders token) throws Exception {
        String authorization = token.getFirst("Authorization");
        log.info("현재 로그인한 유저의 accessToken = " + authorization);
        KakaoGetUserDto kakaoUser = kakaoService.createKakaoUser(authorization);
        User user = userRepository.findByKakaoId(kakaoUser.getKakaoId()).orElseThrow(()
                -> new NotFoundUserException("회원정보가 존재하지 않습니다."));
        return user;
    }


    @Transactional
    public Message deleteUser(HttpHeaders token) throws Exception {
        String authorization = token.getFirst("Authorization");
        KakaoGetUserDto kakaoUser = kakaoService.createKakaoUser(authorization);
        userRepository.deleteByKakaoId(kakaoUser.getKakaoId());
        return new Message("유저가 탈퇴 완료");
    }
}
