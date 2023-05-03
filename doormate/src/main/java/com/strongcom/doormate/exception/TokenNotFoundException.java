package com.strongcom.doormate.exception;

/**
 * AccessToken, RefreshToken 이 존재하지 않는 경우
 * 에러 처리
 */
public class TokenNotFoundException extends RuntimeException {

    /**
     * Constructs an {@code TokenNotFoundException} with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    public TokenNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an {@code TokenNotFoundException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public TokenNotFoundException(String msg) {
        super(msg);
    }
}
