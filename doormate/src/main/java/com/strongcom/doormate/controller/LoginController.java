package com.strongcom.doormate.controller;

import com.strongcom.doormate.crypto.AES256Cipher;
import com.strongcom.doormate.security.jwt.payload.request.LoginRequest;
import com.strongcom.doormate.security.jwt.payload.response.AccessTokenResponse;
import com.strongcom.doormate.security.jwt.payload.response.TokenResponse;
import com.strongcom.doormate.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class LoginController {


    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final RestTemplate restTemplate;

    private final CookieUtil cookieUtil;


    @Value("${jwt.user-cookie-name}")
    private String userCookieName;


    @Value("${jwt.access-token-cookie-name}")
    private String accessTokenCookieName;


    @Value("${jwt.refresh-token-cookie-name}")
    private String refreshTokenCookieName;




    /**
     * 로그인
     *
     * <p>
     *     1.인증서버에 AccessToken, RefreshToken 발급요청
     *     2.발급된 토큰 정보에 대한 쿠키 생성
     * </p>
     *
     *
     * @param httpServletRequest HttpServletRequest
     * @param httpServletResponse HttpServletResponse
     * @param loginRequest set username and password
     * @return AccessTokenResponse accessToken
     * @throws Exception
     */
    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signin(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , @Valid @RequestBody LoginRequest loginRequest) throws Exception {

        CookieUtil cookieUtil = new CookieUtil();

        // 토큰 발행 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();


        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(loginRequest), headers);

        ResponseEntity<HashMap> responseEntity = restTemplate.postForEntity( "http://localhost:8080/token/issue", request , HashMap.class );


        // 토큰 발행 요청 결과
        String accessToken = null;
        String refreshToken = null;

        Cookie userCookie = null;
        Cookie accessTokenCookie = null;
        Cookie refreshTokenCookie = null;

        if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
            accessToken = (String) responseEntity.getBody().get("accessToken");
            refreshToken = (String) responseEntity.getBody().get("refreshToken");

            // User 쿠키 설정
            // Token 쿠키가 다 삭제되는 경우 db, Redis 서버에 존재하는 토큰 정보를 삭제 처리를 위해서 username 을 설정한다.
            // 단 토큰 삭제 처리 후 User 쿠키도 삭제 처리 해야 한다.
            userCookie = cookieUtil.createCookie(userCookieName, AES256Cipher.encrypt(loginRequest.getUsername()), "");

            // AccessToken 쿠키 저장
            accessTokenCookie = cookieUtil.createCookie(accessTokenCookieName, accessToken, "A");

            // RefreshToken 쿠키 저장
            refreshTokenCookie = cookieUtil.createCookie(refreshTokenCookieName, AES256Cipher.encrypt(refreshToken), "R");

            httpServletResponse.addCookie(userCookie);
            httpServletResponse.addCookie(accessTokenCookie);
            httpServletResponse.addCookie(refreshTokenCookie);

            logger.debug("======================================================================================================================");
            logger.debug("Log in : AccessToken({}), RefreshToken({}) 발급 완료", accessToken, refreshToken);
            logger.debug("======================================================================================================================");
        }

        return new ResponseEntity<>(new AccessTokenResponse(accessToken), HttpStatus.OK);
    }

    /**
     * 로그아웃
     *
     * @param httpServletRequest HttpServletRequest
     * @param httpServletResponse HttpServletResponse
     * @return
     * @throws Exception
     */
    @PostMapping("/signout")
    public ResponseEntity<String> signout(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse) throws Exception {

        // 사용자 쿠키 삭제
        Cookie userCookie = cookieUtil.expireCookie(userCookieName);

        // AccessToken 쿠키 삭제
        Cookie accessTokenCookie = cookieUtil.expireCookie(accessTokenCookieName);

        // RefreshToken 쿠키 삭제
        Cookie refreshTokenCookie = cookieUtil.expireCookie(refreshTokenCookieName);

        httpServletResponse.addCookie(userCookie);
        httpServletResponse.addCookie(accessTokenCookie);
        httpServletResponse.addCookie(refreshTokenCookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
