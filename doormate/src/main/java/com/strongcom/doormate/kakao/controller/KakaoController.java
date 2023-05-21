package com.strongcom.doormate.kakao.controller;

import com.strongcom.doormate.kakao.dto.KakaoGetTokenDto;
import com.strongcom.doormate.kakao.dto.KakaoGetUserDto;
import com.strongcom.doormate.kakao.dto.KakaoSetUserNameDto;
import com.strongcom.doormate.kakao.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;


    // 회원정보 요청

    // 회원가입
    @PostMapping("/add")
    public ResponseEntity<String> getKakaoUser(@RequestBody KakaoGetTokenDto kakaoGetTokenDto) throws Exception {
        KakaoGetUserDto kakaoUser = kakaoService.createKakaoUser(kakaoGetTokenDto.getAccessToken());
        System.out.println(kakaoUser.getKakaoId());
        String message = kakaoService.joinKakaoUser(kakaoGetTokenDto.getTargetToken(), kakaoUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }

    // 유저네임 요청
    @PatchMapping(value = "/user")
    public ResponseEntity<String> getUserName(@RequestHeader HttpHeaders token, @RequestBody KakaoSetUserNameDto kakaoSetUserNameDto) throws Exception {
        String authentication = token.getFirst("Authorization");
        System.out.println("authentication = " + authentication);
        KakaoGetUserDto kakaoUser = kakaoService.createKakaoUser(authentication);
        String message = kakaoService.setUserName(kakaoUser.getKakaoId(), kakaoSetUserNameDto.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(message);
    }

}
