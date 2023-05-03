package com.strongcom.doormate.controller;

import com.strongcom.doormate.crypto.AES256Cipher;
import com.strongcom.doormate.security.jwt.payload.request.LoginRequest;
import com.strongcom.doormate.security.jwt.payload.response.TokenResponse;
import com.strongcom.doormate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;

    /**
     * 토큰 발급
     *
     * @param loginRequest 토큰 발급을 위한 ID, 비밀번호
     * @return ResponseEntity<Map<String,Object>> AccessToken, RefreshToken
     */
    @PostMapping("/issue")
    public ResponseEntity<Map<String,Object>> issueToken(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        TokenResponse tokenResponse = authService.generateAccessTokenAndRefreshToken(loginRequest);

        Map<String,Object> retObj = new HashMap<>();
        retObj.put("accessToken", tokenResponse.getAccessToken());
        retObj.put("refreshToken", tokenResponse.getRefreshToken());

        return new ResponseEntity<>(retObj, HttpStatus.OK);
    }

    /**
     * AccessToken 재발급
     *
     * @param refreshToken Base64로 암호화된 값이므로 서비스 호출전 Decoding 처리 해얗 함
     * @return ResponseEntity<String> 재발급된 accessToken
     */
    @PostMapping("/re-issue/access-token")
    public ResponseEntity<String> reIssueAccessToken(@RequestBody String refreshToken) throws Exception {
        String accessToken = authService.generateAccessTokenFromRefreshToken(AES256Cipher.decrypt(refreshToken));

        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    /**
     * RefreshToken 재발급
     *
     * @param accessToken 발급요청한 accessToken
     * @return ResponseEntity<String> 재발급된 refreshToken
     */
    @PostMapping("/re-issue/refresh-token")
    public ResponseEntity<String> reIssueRefreshToken(@RequestBody String accessToken) throws Exception {
        return new ResponseEntity<>(authService.generateRefreshTokenFromAccessToken(accessToken), HttpStatus.OK);
    }

    /**
     * RefreshToken 유효성 체크
     *
     * @param refreshToken Base64로 암호화된 값이므로 서비스 호출전 Decoding 처리 해얗 함
     * @return ResponseEntity<Boolean> true or false
     */
    @PostMapping("/validate/refresh-token")
    public ResponseEntity<Boolean> validateRefreshToken(@RequestBody String refreshToken) throws Exception {
        return new ResponseEntity<>(authService.validateRefreshToken(AES256Cipher.decrypt(refreshToken)), HttpStatus.OK);
    }

    @PostMapping("/remove/refresh-token")
    public ResponseEntity<String> removeRefreshToken(@RequestBody String username) throws UsernameNotFoundException {
        authService.deleteTokenFromUsername(AES256Cipher.decrypt(username));

        return  new ResponseEntity<>("토큰 정보가 정상적으로 삭제 되었습니다.", HttpStatus.OK);
    }
}
