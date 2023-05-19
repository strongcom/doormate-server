package com.strongcom.doormate.kakao.controller;


import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.kakao.dto.GetUserInfoResponse;
import com.strongcom.doormate.kakao.service.KakaoService;
import com.strongcom.doormate.kakao.dto.GetLoginTokenResponse;
import com.strongcom.doormate.kakao.dto.GetkakaoTokenRequest;
import com.strongcom.doormate.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    private final UserRepository userRepository;


    @PostMapping("/kakao")
    public ResponseEntity<GetLoginTokenResponse> getKaKaoToken(@Valid @RequestBody GetkakaoTokenRequest getkakaoToken) throws IOException, org.json.simple.parser.ParseException {
        String code = getkakaoToken.getCode();
        String redirectUrl = getkakaoToken.getRedirectUrl();
        GetLoginTokenResponse getLoginToken = kakaoService.KakaoLogin(code, redirectUrl);
        return new ResponseEntity(getLoginToken, HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<GetUserInfoResponse> getUserInfo(@RequestAttribute Claims claims) {
        //엑세스 토큰안의 유저 아이디로 유저를 찾은 다음 유저정보 리턴해줌
        Integer userId = (int) claims.get("userId");
        Long longId = Long.valueOf(userId);
        //userId로 유저 꺼내기
        Optional<User> result = userRepository.findById(longId);
        User user = result.get();

        GetUserInfoResponse getUserInfo = GetUserInfoResponse.builder()
                .userId(user.getUserId())
                .build();

        return new ResponseEntity(getUserInfo, HttpStatus.OK);
    }
}
