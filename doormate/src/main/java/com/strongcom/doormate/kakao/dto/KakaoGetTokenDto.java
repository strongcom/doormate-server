package com.strongcom.doormate.kakao.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class KakaoGetTokenDto {
    private String accessToken;
    private String refreshToken;
    private String targetToken;
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String accessTokenExpiresAt;

//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String refreshTokenExpiresAt;
    private List<String> scope;
    private String idToken;
}
