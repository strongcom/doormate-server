package com.strongcom.doormate.controller;

import com.strongcom.doormate.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
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
    private final RestTemplate restTemplate;


    @PostMapping("/add")
    public ResponseEntity<UserDto> createUser(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , @Valid @RequestBody UserDto userDto) throws Exception {

        //HttpEntity 생성
        HttpEntity<UserDto> request = new HttpEntity<>(userDto);

        //API 호출
        ResponseEntity<UserDto> responseEntity = restTemplate.postForEntity( "http://localhost:8080/user/register", request , UserDto.class);


        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);

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


}
