package com.strongcom.doormate.controller;

import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.UserDto;
import com.strongcom.doormate.dto.UserInfoRespDto;
import com.strongcom.doormate.kakao.service.KakaoService;
import com.strongcom.doormate.repository.UserRepository;
import com.strongcom.doormate.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final RestTemplate restTemplate;

    private final UserServiceImpl userService;




    @PostMapping("/add")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto);

        return new ResponseEntity<>(userDto, HttpStatus.OK);

    }


    @DeleteMapping("/delete")
    public ResponseEntity<UserDto> deleteUser(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , @Valid @RequestBody UserDto userDto) throws Exception {

        //HttpEntity 생성
        HttpEntity<UserDto> request = new HttpEntity<>(userDto);

        //API 호출
        ResponseEntity<UserDto> responseEntity = restTemplate.postForEntity( "http://localhost:8080/user/withdraw", request , UserDto.class);


        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);

    }

    @GetMapping()
    public ResponseEntity<UserInfoRespDto> getUserNameAndNickname(@RequestHeader HttpHeaders token) throws Exception {
        User user = userService.findByKakaoUser(token);
        UserInfoRespDto userInfoRespDto= UserInfoRespDto.builder()
                .userName(user.getUsername())
                .nickName(user.getNickname())
                .image_url(user.getImage_url())
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(userInfoRespDto);
    }


}
