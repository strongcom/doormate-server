package com.strongcom.doormate.kakao.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class KakaoGetUserDto {
    private Long kakaoId;
    private String nickName;
    private String image;
}
