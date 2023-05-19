package com.strongcom.doormate.kakao.exception;

import com.strongcom.doormate.kakao.exception.StatusEnum;

public class TokenNotFoundException extends IllegalArgumentException {
    private final StatusEnum status;
    private static final String message = "해당 아이디를 가진 유저가 없습니다. 아이디 값을 다시 한번 확인하세요.";

    public TokenNotFoundException() {
        super();
        this.status = StatusEnum.TOKEN_NOT_EXIST;
    }
}
