package com.strongcom.doormate.service;

import com.strongcom.doormate.security.jwt.payload.request.LoginRequest;
import com.strongcom.doormate.security.jwt.payload.response.TokenResponse;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * 토큰 관리 및 사용자 인증 관리를 위한 인터페이스
 */
public interface AuthService {

    /**
     * 토큰발행
     *
     * @param loginRequest - UsernamePasswordAuthenticationToken 객체 생성을 위한 파라미터 정보를 담고 있는 클래스
     * 파라미터정보: username,password
     * @return TokenDto - accessToken, refreshToken 정보를 담고 있는 객체
     * @exception  BadCredentialsException - 사용자 정보가 올바르지 않은 경우 예외 발생
     */
    public TokenResponse generateAccessTokenAndRefreshToken(LoginRequest loginRequest) throws Exception;

    /**
     * AccessToken 재발행
     * <p>
     * accessToken 이 만료되고 refreshToken 이 유효한 경우
     * </p>
     *
     * @param refreshToken - 토큰 값은 Apache Common Base64 Decoding 처리 되어야한다.
     * @return accessToken
     */
    public String generateAccessTokenFromRefreshToken(String refreshToken) throws Exception;

    /**
     * RefreshToken 재발행
     * <p>
     * accessToken 은 유효하고 refreshToken 이 만료된 경우
     * </p>
     *
     * @param accessToken 현재 유효한 accessToken
     * @return refreshToken
     */
    public String generateRefreshTokenFromAccessToken(String accessToken) throws Exception;

    /**
     * AccessToken 유효성 체크
     *
     * @param accessToken the accessToken
     * @return true or false
     */
    public boolean validateAccessToken(String accessToken);

    /**
     * RefreshToken 유효성 체크
     *
     * @param refreshToken - 토큰 값은 Apache Common Base64 Decoding 처리 되어야한다.
     * @return set true of false
     */
    public boolean validateRefreshToken(String refreshToken);

    /**
     * 토큰 만료시 mysqldb, Redis에 저장된 토큰 정보 삭제 처리
     *
     * @param username 로그인 사용자
     */
    public void deleteTokenFromUsername(String username);

}
