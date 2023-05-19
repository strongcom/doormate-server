package com.strongcom.doormate.kakao.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserInfoResponse {

    private Long userId;

    /**
     * Oauth 에서 제공하는 아이디
     */
    private String providerId;




    @Builder
    public GetUserInfoResponse(Long userId, String providerId) {
        this.userId = userId;
        this.providerId = providerId;
    }
}
