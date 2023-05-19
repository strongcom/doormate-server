package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.Authority;
import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.UserDto;
import com.strongcom.doormate.exception.NotFoundUserException;
import com.strongcom.doormate.kakao.dto.AddInfoRequest;
import com.strongcom.doormate.repository.UserRepository;
import com.strongcom.doormate.service.UserService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User createUser(UserDto userDto) {
        if(userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 사용자 입니다.");
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

    public void addUserInfo(AddInfoRequest addInfoRequest, Long userId){
        Optional<User> byId = userRepository.findById(userId);

        User user = byId.get();

        user.setUsername(addInfoRequest.getUsername());
        user.setTargetToken(addInfoRequest.getTargetToken());

        userRepository.save(user);

    }
}
