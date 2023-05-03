package com.strongcom.doormate.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 *  토큰생성, 토큰정보조회, 유효성체크를 위한 클래스
 */
@Component
public class JwtUtil implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;

    private Key key;

    /**+
     * 기본생성자
     * 
     * @param secret set secret key from application.yml in jwt.secret
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    /**+
     *
     * @param token user token
     * @return validate of token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 토큰 정보 조회
     * 
     * @param token user token
     * @return Authentication
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();


        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 생성자에 주입받은 secret key decoding 처리
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        
        // decoding secret key key 변수에 할당
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
}

