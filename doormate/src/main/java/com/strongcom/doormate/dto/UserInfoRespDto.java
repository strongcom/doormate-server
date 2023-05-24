package com.strongcom.doormate.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfoRespDto {

    private String userName;
    private String nickName;
    private String image_url;

}
