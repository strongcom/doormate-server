package com.strongcom.doormate.jwt;

import com.strongcom.doormate.crypto.AES256Cipher;
import com.strongcom.doormate.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    // HEADER 에 Token 을 설정하는 경우 변수명
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Filter 클래스가 두번 호출 되는 것을 방지하기 위한 변수
    private static final String FILTER_APPLIED = "JwtFilterApplied";

    // User 쿠키명
    private String USER_COOKIE_NAME = "userCookie";

    // AccessToken 쿠키명
    private String ACCESS_TOKEN_COOKIE_NAME = "accessTokenCookie";

    // RefreshToken 쿠키명
    private String REFRESH_TOKEN_COOKIE_NAME = "refreshTokenCookie";

    // AccessToken 사용자 정보 조회를 위한 클래스
    private JwtUtil jwtUtil;

    // AuthServer 와 연동시 사용하기 위한 클래스
    private RestTemplate restTemplate;

    // AccessToken 정보를 저장하기 위한 클래스
    private CookieUtil cookieUtil;

    // Token 사용자 정보를 위한 Key
    private static final String AUTHORITIES_KEY = "auth";

    public JwtFilter(JwtUtil jwtUtil, RestTemplate restTemplate, CookieUtil cookieUtil) {
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException  {

        // Filter 가 두번씨 호출 되는 부분을 처리하기 위한 로직
        if (httpServletRequest.getAttribute(FILTER_APPLIED) != null) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return ;
        }

        // Request URI
        String requestURI = httpServletRequest.getRequestURI();

        /******************************************************************************************************************************
         * 토큰 유효성 체크                                                                                                           *
         *                                                                                                                            *
         * - 로그인시 저장된 쿠키 정보에서 AccessToken, RefreshToken 값의 유효성을 체크한다.                                          *
         * - RefreshToken 정보는 Redis 서버에 저장되어 있으므로 쿠키값에 저장된 키로 인증서버를 통하여 유효성을 체크한다.             *
         ******************************************************************************************************************************/
        // 토큰 유효성 체크
        boolean isValidAccessToken = false;
        boolean isValidRefreshToken = false;

        // 쿠키 조회
        final Cookie userCookie = cookieUtil.getCookie(httpServletRequest, USER_COOKIE_NAME);
        final Cookie accessTokenCookie = cookieUtil.getCookie(httpServletRequest, ACCESS_TOKEN_COOKIE_NAME);
        final Cookie refreshTokenCookie = cookieUtil.getCookie(httpServletRequest, REFRESH_TOKEN_COOKIE_NAME);

        // AccessToken 설정 및 유효성 체크
        String accessToken = null;

        if (null != accessTokenCookie)  {
            accessToken = accessTokenCookie.getValue();

            // AccessToken 유효성 체크
            logger.debug("AccessToken({}) 유효성 체크", accessToken);
            isValidAccessToken = validateAccessToken(accessToken);
        }

        // RefreshToken 유효성 체크
        String refreshToken = null;

        if (null != refreshTokenCookie) {
            refreshToken = refreshTokenCookie.getValue();

            // RefreshToken 유효성 체크
            logger.debug("RefreshToken({}) 유효성 체크", refreshToken);
            isValidRefreshToken = validateRefreshToken(refreshTokenCookie.getValue());
        }
        logger.debug("======================================================================================================================");
        logger.debug("토큰 유효성 및 쿠키 체크");
        logger.debug("======================================================================================================================");
        logger.debug("isValidAccessToken({}), isValidRefreshToken({}), userCookie({}), uri({})", isValidAccessToken, isValidRefreshToken, userCookie, requestURI);
        logger.debug("======================================================================================================================");

        /******************************************************************************************************************************/

        /******************************************************************************************************************************
         * 토큰의 유효성 여부에 따른 로직처리                                                                                         *
         *                                                                                                                            *
         * 1. isValidAccessToken(false), isValidRefreshToken(false)                                                                   *
         *    - jwtAccessDeniedHandler 실행                                                                                           *
         * 2. isValidAccessToken(false), isValidRefreshToken(true)                                                                    *
         *    - AccessToken 재발급                                                                                                    *
         * 3. isValidAccessToken(true), isValidRefreshToken(false)                                                                    *
         *    - RefreshToken 재발급                                                                                                   *
         * 4. isValidAccessToken(true), isValidRefreshToken(true)                                                                     *
         *    - 사용자 Request 수행                                                                                                   *
         ******************************************************************************************************************************/
        // 토큰정보가 없고 사용자 쿠키만 존재하고 로그 아웃이 아닌경우 토큰 정보 삭제 처리
        if (!isValidAccessToken && !isValidRefreshToken && userCookie != null && !requestURI.equals("/signout")) {
            // 토큰 정보 삭제
            deleteToken(userCookie.getValue());

            // 사용자 쿠키 정보 삭제
            CookieUtil cookieUtil = new CookieUtil();
            httpServletResponse.addCookie(cookieUtil.expireCookie(USER_COOKIE_NAME));
        }

        // AccessToken 재발급인 경우
        if (!isValidAccessToken && isValidRefreshToken) {
            // AccessToken 재발급
            accessToken = reIssueAccessToken(refreshToken);

            // 쿠키 생성
            CookieUtil cookieUtil = new CookieUtil();
            httpServletResponse.addCookie(cookieUtil.createCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, "A"));

            // 재발급 AccessToken 유효성 체크
            isValidAccessToken = validateAccessToken(accessToken);
        }

        // RefreshToken 재발급인 경우
        if (isValidAccessToken && !isValidRefreshToken) {
            // RefreshToken 재발급
            refreshToken = reIssueRefreshToken(accessToken);

            // RefreshToken 쿠키 생성
            CookieUtil refreshTokenCookieUtil = new CookieUtil();
            httpServletResponse.addCookie(cookieUtil.createCookie(REFRESH_TOKEN_COOKIE_NAME, AES256Cipher.encrypt(refreshToken), "R"));
        }

        // AccessToken 이 유효한 경우 SecurityContextHolder 에 사용자 정보 설정
        if (isValidAccessToken) {
            // 토큰 정보 조회
            Authentication authentication = getAuthentication(accessToken);

            // URL 권한 처리를 위해서 SecurityContextHolder 에 사용자 정보와 역할 정보를 설정
            // URL 에서 역할 설정은 @PreAuthorize("hasAnyRole('USER','ADMIN')") 통하여 제어 할 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        httpServletRequest.setAttribute(FILTER_APPLIED,true);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * AccessToken 유효성 체크
     *
     * @param accessToken
     * @return true or false
     */
    private Boolean validateAccessToken(String accessToken) {
        return jwtUtil.validateToken(accessToken);
    }

    /**
     * RefreshToken 유효성 체크
     *
     * @param refreshToken
     * @return true or false
     */
    private boolean validateRefreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(refreshToken, headers);
        ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity( "http://localhost:8080/token/validate/refresh-token", request , Boolean.class);

        return responseEntity.getBody();
    }

    /**
     * 토근정보 조회
     *
     * @param accessToken
     * @return Authentication 사용자정보
     */
    private Authentication getAuthentication(String accessToken) {
        return jwtUtil.getAuthentication(accessToken);
    }

    /**
     * AccessToken 재발급
     *
     * @param refreshToken
     * @return refreshToken 재발급 AccessToken
     */
    private String reIssueAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(refreshToken, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity( "http://localhost:8080/token/re-issue/access-token", request , String.class);

        return responseEntity.getBody();
    }

    /**
     * RefreshToken 재발급
     *
     * @param accessToken 유효한 accessToken
     */
    private String reIssueRefreshToken(String accessToken) {
        String refreshToken = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(accessToken, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity( "http://localhost:8080/token/re-issue/refresh-token", request , String.class);

        refreshToken = responseEntity.getBody();

        return refreshToken;
    }

    /**
     * Token 삭제
     *
     * @param username 로그인 사용자ID
     */
    private void deleteToken(String username) {
        String refreshToken = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(username, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity( "http://localhost:8080/token/remove/refresh-token", request , String.class);

        refreshToken = responseEntity.getBody();
    }
}
