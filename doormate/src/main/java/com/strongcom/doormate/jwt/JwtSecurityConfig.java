package com.strongcom.doormate.jwt;

import com.strongcom.doormate.security.jwt.TokenProvider;
import com.strongcom.doormate.util.CookieUtil;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    private JwtUtil jwtUtil;

    private RestTemplate restTemplate;

    private CookieUtil cookieUtil;

    private TokenProvider tokenProvider;

    public JwtSecurityConfig(JwtUtil jwtUtil, RestTemplate restTemplate, CookieUtil cookieUtil, TokenProvider tokenProvider) {
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        this.cookieUtil = cookieUtil;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtFilter customFilter = new JwtFilter(jwtUtil, restTemplate, cookieUtil, tokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
