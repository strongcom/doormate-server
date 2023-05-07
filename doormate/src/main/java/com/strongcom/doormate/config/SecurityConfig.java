package com.strongcom.doormate.config;

import com.strongcom.doormate.jwt.*;
import com.strongcom.doormate.jwt.JwtAccessDeniedHandler;
import com.strongcom.doormate.jwt.JwtAuthenticationEntryPoint;
import com.strongcom.doormate.jwt.JwtSecurityConfig;
import com.strongcom.doormate.jwt.JwtUtil;
import com.strongcom.doormate.security.jwt.TokenProvider;
import com.strongcom.doormate.util.CookieUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final CookieUtil cookieUtil;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private TokenProvider tokenProvider;


    public SecurityConfig(TokenProvider tokenProvider, JwtUtil jwtUtil, RestTemplate restTemplate, CookieUtil cookieUtil, CorsFilter corsFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        this.cookieUtil = cookieUtil;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(
                        "/**"
                        , "/favicon.ico"
                );
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()


                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/token/issue").permitAll()
                .antMatchers("/token/re-issue/access-token").permitAll()
                .antMatchers("/token/re-issue/refresh-token").permitAll()
                .antMatchers("/token/validate/refresh-token").permitAll()
                .antMatchers("/token/remove/refresh-token").permitAll()

                .antMatchers("/user/register").permitAll()
                .antMatchers("/signin").permitAll()
                .antMatchers("/signout").permitAll()
                .antMatchers("/user/add").permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(jwtUtil, restTemplate, cookieUtil, tokenProvider));
    }
}

