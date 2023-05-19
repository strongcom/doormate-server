package com.strongcom.doormate.controller;

import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.UserDto;
import com.strongcom.doormate.kakao.dto.AddInfoRequest;
import com.strongcom.doormate.kakao.dto.CommonResponse;
import com.strongcom.doormate.service.UserService;
import com.strongcom.doormate.service.impl.UserServiceImpl;
import io.jsonwebtoken.Claims;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final RestTemplate restTemplate;

    private final UserService userService;


    @PostMapping("/add")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto);

        return new ResponseEntity<>(userDto, HttpStatus.OK);

    }

    @PostMapping("/addInfo")
    public ResponseEntity<CommonResponse> addUserInfo(@Valid @RequestBody AddInfoRequest addInfoRequest, @RequestAttribute Claims claims) {
        Integer userId = (int) claims.get("userId");
        Long longId = Long.valueOf(userId);
        userService.addUserInfo(addInfoRequest, longId);

        CommonResponse response = new CommonResponse("유저 정보 추가에 성공했습니다.");
        return new ResponseEntity(response, HttpStatus.OK);
    }



}
