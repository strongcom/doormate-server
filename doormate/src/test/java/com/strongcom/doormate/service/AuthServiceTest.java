package com.strongcom.doormate.service;

import com.strongcom.doormate.DoormateApplication;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.UserDto;
import com.strongcom.doormate.repository.UserRepository;
import com.strongcom.doormate.security.jwt.payload.request.LoginRequest;
import com.strongcom.doormate.security.jwt.payload.response.TokenResponse;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DoormateApplication.class)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSuccessGenerateAccessTokenAndRefreshToken() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("admin")
                .password("admin")
                .targetToken("null")
                .build();

        TokenResponse tokenResponse = authService.generateAccessTokenAndRefreshToken(request);
        Assert.assertNotNull(tokenResponse);
    }

    @Test
    public void 회원가입() throws Exception {
        UserDto dto = UserDto.builder()
                .username("string")
                .password("string")
                .nickname("string")
                //.targetToken("08ZjukF9LXr9SDGEj52mOr9f2mjX77AH9")
                .build();


        User user = userService.createUser(dto);
        userRepository.save(user);
    }



}