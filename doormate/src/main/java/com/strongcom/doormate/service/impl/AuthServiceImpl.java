package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.crypto.AES256Cipher;
import com.strongcom.doormate.domain.RefreshToken;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.exception.TokenNotFoundException;
import com.strongcom.doormate.repository.RefreshTokenRepository;
import com.strongcom.doormate.repository.UserRepository;
import com.strongcom.doormate.security.jwt.TokenProvider;
import com.strongcom.doormate.security.jwt.payload.request.LoginRequest;
import com.strongcom.doormate.security.jwt.payload.response.TokenResponse;
import com.strongcom.doormate.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    public TokenResponse generateAccessTokenAndRefreshToken(LoginRequest loginRequest) throws Exception {
        // 1. accessToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication = null;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("[" + loginRequest.getUsername() + "] 사용자 정보가 존재하지 않습니다.");
            // BadCredentialsException -> 아이디가 존재하지 않거나, 비밀번호가 틀린 경우 발생
        }
        String accessToken = tokenProvider.createToken(authentication);
        logger.debug("AccessToken({}) 이 정상적으로 발급 되었습니다.", accessToken);

        // 2. refreshToken 생성
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 60); // 60분 뒤 설정

        RefreshToken refreshTokenRequest = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .username(loginRequest.getUsername())
                .password(AES256Cipher.encrypt(loginRequest.getPassword()))
                .expiryDate(c.getTime())
                .build();

        RefreshToken refreshTokenResponse = refreshTokenRepository.save(refreshTokenRequest);
        logger.debug("RefreshToken({}) 이 정상적으로 발급되었습니다.", refreshTokenResponse.getId());

        // RefreshToken 재발급후 AccessToken 재발급시 Encoding 되지 않은 사용자의 비밀번호가 필요 한데
        // Encoding 전의 비밀번호는 Redis 서버에에 저장 하고 있는데 RefreshToken 만료시 쿠키 정보가 삭제
        // 되므로 사용자의 비밀번호를 알 수 없으므로 사용자 테이블에 RefreshToken 발급전 토큰 정보를 업데이트 처리한다.
        Optional<User> user =  userRepository.findOneWithAuthoritiesByUsername(loginRequest.getUsername());
        user.get().setToken(AES256Cipher.encrypt(refreshTokenResponse.getId()));
        userRepository.save(user.get());

        // 3. return 객체 생성
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenResponse.getId())
                .authentication(authentication)
                .build();

        return tokenResponse;
    }

    @Override
    public String generateAccessTokenFromRefreshToken(String refreshToken) throws Exception {
        String accessToken = null;

        // 전달받은 RefreshToken 정보로 Redis 서버에 저장된 사용자 ID 조회
        logger.debug("refreshToken({})", refreshToken);
        Optional<RefreshToken> token = Optional.ofNullable(refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("RefreshToken 이 존재하지 않습니다.")));

        // AccessToken 재발행
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(token.get().getUsername(),
                AES256Cipher.decrypt(token.get().getPassword()));

        Authentication authentication = null;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("[" + token.get().getUsername() + "] 사용자 정보가 존재 하지 않습니다.");
        }

        accessToken = tokenProvider.createToken(authentication);
        logger.debug("AccessToken({}) 이 정상적으로 발급 되었습니다.", accessToken);

        return accessToken;
    }

    @Override
    public String generateRefreshTokenFromAccessToken(String accessToken) throws Exception {
        // 1. AccessToken 유효성 체크
        boolean isAccessToken = validateAccessToken(accessToken);
        logger.debug("AccessToken({}) 유효성 여부 ({})", accessToken, isAccessToken);

        // 2. accessToken 이 유효하고 refreshToken 이 만료된 경우 refreshToken 을 재발급한다.
        RefreshToken refreshTokenResponse = null;

        if (isAccessToken) {
            // 토큰 사용자 정보 조회
            Authentication authentication = tokenProvider.getAuthentication(accessToken);

            // Expired RefreshToken ID 조회
            // Cookie 정보가 삭제 되기 때문에 쿠키에선느 토큰 정보를 알수 없기 때문에 사용자 정보에서 RefreshToken 정보를 조회 한다.
            String expiredRefreshTokenId = AES256Cipher.decrypt(userRepository.findOneWithAuthoritiesByUsername(authentication.getName()).get().getToken());
            logger.debug("Expired RefreshToken({})", expiredRefreshTokenId);

            // Expired RefreshToken 조회
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(expiredRefreshTokenId);

            // refreshToken 발급
            Calendar c = Calendar.getInstance();

            c.add(Calendar.MINUTE, 60);

            RefreshToken refreshTokenRequest = RefreshToken.builder()
                    .id(UUID.randomUUID().toString())
                    .username(authentication.getName())
                    .password(refreshToken.get().getPassword())
                    .expiryDate(c.getTime())
                    .build();

            refreshTokenResponse = refreshTokenRepository.save(refreshTokenRequest);

            // 재발급 RefreshTokenId 업데이트
            Optional<User> user =  userRepository.findOneWithAuthoritiesByUsername(authentication.getName());
            user.get().setToken(AES256Cipher.encrypt(refreshTokenResponse.getId()));
            userRepository.save(user.get());

            // RefreshToken 재발급이 완료 된 경우 기존키는 삭제 처리한다.
            refreshTokenRepository.deleteById(expiredRefreshTokenId);
            logger.debug("Expired RefreshToken({}) 삭제 처리 되었습니다.", expiredRefreshTokenId);

            logger.debug("RefreshToken({}) 이 정상적으로 재발급 되었습니다.", refreshTokenResponse.getId());
        } else {
            throw new IllegalArgumentException("AccessToken 이 유효 하지 않습니다.");
        }

        return refreshTokenResponse.getId();
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        return tokenProvider.validateToken(accessToken);
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {

        // 유효성여부
        boolean isValidate = false;

        // 현재날짜시간
        Date d1 = null;

        // Redis 서버에 저장된 만료날짜시간
        Date d2 = null;

        // 현재날짜시간
        Date currentTime = new Date();

        // RefreshToken 정보 조회
        Optional<RefreshToken> token = refreshTokenRepository.findById(refreshToken);

        // 토큰이 존재하는 경우 현재날짜와 만료날짜를 비교 하여 유효성 체크
        if (token.isPresent()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                d1 = f.parse(sdf.format(currentTime));
                d2 = f.parse(sdf.format(token.get().getExpiryDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // 현재시간이 설정만료시간보다 작은 경우 토큰값은 유효하므로 true 설정
            if (d1.compareTo(d2) < 0) {
                isValidate = true;
            }
        }

        return isValidate;
    }

    @Override
    public void deleteTokenFromUsername(String username) {

        // 사용자정보 조회
        Optional<User> user = Optional.ofNullable(userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자정보가 존재하지 않습니다.")));

        // 만료 RefreshToken
        String expireRefreshToken = AES256Cipher.decrypt(user.get().getToken());

        // db 토큰 정보 업데이트
        user.get().setToken("");
        userRepository.save(user.get());

        // Redis 토큰 정보 삭제
        refreshTokenRepository.deleteById(expireRefreshToken);

    }
}
