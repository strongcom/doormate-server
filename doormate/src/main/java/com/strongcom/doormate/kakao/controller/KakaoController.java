package com.strongcom.doormate.kakao.controller;

import com.strongcom.doormate.kakao.dto.KakaoGetTokenDto;
import com.strongcom.doormate.kakao.dto.KakaoGetUserDto;
import com.strongcom.doormate.kakao.dto.KakaoSetUserNameDto;
import com.strongcom.doormate.kakao.service.KakaoService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "회원가입", notes = "로그인 정보 존재 시 204, 로그인정보 없을 시 200 -> userName 요청")
    @PostMapping("/add")
    public ResponseEntity<String> getKakaoUser(@RequestBody KakaoGetTokenDto kakaoGetTokenDto) throws Exception {
        KakaoGetUserDto kakaoUser = kakaoService.createKakaoUser(kakaoGetTokenDto.getAccessToken());
        String message = kakaoService.joinKakaoUser(kakaoGetTokenDto.getTargetToken(),
                kakaoGetTokenDto.getRefreshToken(), kakaoUser);
        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }

    // 유저네임 요청
    @PatchMapping(value = "/user")
    @ApiOperation(value = "카카오 회원가입(유저정보 입력)", notes = "유저네임 추가 요청, 회원가입 성공시 201 create, 이미 존재 시 409 응답")
    public ResponseEntity<String> getUserName(@RequestHeader HttpHeaders token, @RequestBody KakaoSetUserNameDto kakaoSetUserNameDto) throws Exception {
        String authentication = token.getFirst("Authorization");
        KakaoGetUserDto kakaoUser = kakaoService.createKakaoUser(authentication);
        String message = kakaoService.setUserName(kakaoUser.getKakaoId(), kakaoSetUserNameDto.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(message);
    }

}
