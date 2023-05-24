package com.strongcom.doormate.kakao.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.strongcom.doormate.domain.Message;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.exception.DuplicateException;
import com.strongcom.doormate.exception.DuplicateUserException;
import com.strongcom.doormate.exception.NotFoundAuthorizationException;
import com.strongcom.doormate.exception.NotFoundUserException;
import com.strongcom.doormate.kakao.dto.KakaoGetUserDto;
import com.strongcom.doormate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final UserRepository userRepository;

    private static final String NOT_TOKEN_EXIST_MESSAGE = "유효한 토큰값이 아닙니다.";
    private static final String SUCCESS_ADD_USER_MESSAGE = "username 등록완료, 회원가입 성공";
    private static final String REQUEST_USERNAME_MESSAGE = "userName 요청";

    public KakaoGetUserDto createKakaoUser(String token) throws Exception {

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        Long id = null;
        String nickName = "";
        String image = "";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", token); //전송할 header 작성, access_token전송

            log.info("유저정보 조회를 위해 카카오로 보낸 access 토큰 : " + token);
            //accessToken이 유효하면 200 OK
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);
            if (responseCode != 200) {
                throw new NotFoundAuthorizationException(NOT_TOKEN_EXIST_MESSAGE);
            }

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            id = element.getAsJsonObject().get("id").getAsLong();
//            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile_nickname_needs_agreement").getAsBoolean();
            nickName = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();
            image = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("profile_image_url").getAsString();

            log.info("id : " + id + ", nickName : " + nickName + ", image = " + image);

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return KakaoGetUserDto.builder()
                .kakaoId(id)
                .nickName(nickName)
                .image(image)
                .build();
    }

    @Transactional
    public Message setUserName(Long userId, String userName) {
        User kakaoUser = userRepository.findByKakaoId(userId).orElseThrow(() -> new NotFoundUserException("존재하지 않는 유저입니다."));
        Optional<User> savedUser = userRepository.findByUsername(userName);
        if (savedUser.isPresent()) {
            throw new DuplicateException("회원정보가 중복됩니다. 다시 설정해주세요");
        }
        kakaoUser.setKakaoUser(userName);

        return new Message(SUCCESS_ADD_USER_MESSAGE);
    }

    public Message joinKakaoUser(String targetToken, String refreshToken, KakaoGetUserDto kakaoGetUserDto) {
        Optional<User> kakaoId = userRepository.findByKakaoId(kakaoGetUserDto.getKakaoId());
        if(kakaoId.isPresent()) throw new DuplicateUserException("가입된 유저입니다.");
        User newUser = User.builder()
                .kakaoId(kakaoGetUserDto.getKakaoId())
                .nickname(kakaoGetUserDto.getNickName())
                .targetToken(targetToken)
                .refreshToken(refreshToken)
                .build();
        User save = userRepository.save(newUser);
        return new Message(REQUEST_USERNAME_MESSAGE);
    }
}
