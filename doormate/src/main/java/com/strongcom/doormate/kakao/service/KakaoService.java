package com.strongcom.doormate.kakao.service;


import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.kakao.JwtTokenProvider;
import com.strongcom.doormate.kakao.dto.GetLoginTokenResponse;
import com.strongcom.doormate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenService tokenService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;


    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String client_secret;


    public GetLoginTokenResponse KakaoLogin(String code, String redirectUrl) throws IOException, ParseException {
        String kakaoAccessToken = this.getKakaoToken(code, redirectUrl);
        Map<String, String> userInfo = this.getKaKaoUserInfo(kakaoAccessToken);
        Optional<User> id = findByProviderId(userInfo.get("id"));
        boolean isNewUser = false;
        if (id.isEmpty()) { // 카카오 계정은 이매일이 카카오에서 주는 아이디값
            User user = new User();
            user.setProviderId(userInfo.get("id"));
            userRepository.save(user);
            isNewUser = true;
            return GetLoginTokenResponse.builder()
                    .accessToken(this.jwtTokenProvider.makeJwtToken(user.getUserId(),1))
                    .refreshToken(tokenService.issueRefreshToken(user.getUserId()))
                    .isNewUser(isNewUser)
                    .build();
        }
        User findUser = id.get();
        return GetLoginTokenResponse.builder()
                .accessToken(this.jwtTokenProvider.makeJwtToken(findUser.getUserId(),1))
                .refreshToken(tokenService.issueRefreshToken(findUser.getUserId()))
                .isNewUser(isNewUser)
                .build();
    }

    public Optional<User> findByProviderId(String providerId) {
            return userRepository.findByProviderId(providerId);
    }

    public Map<String, String> getKaKaoUserInfo(String access_token) {
        String host = "https://kapi.kakao.com/v2/user/me";
        Map<String, String> result = new HashMap<>();

        try {
            URL url = new URL(host);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + access_token);
            urlConnection.setRequestMethod("GET");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))){
                String line;
                StringBuilder res = new StringBuilder();
                while((line=br.readLine())!=null)
                {
                    res.append(line);
                }

                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(res.toString());


                JSONObject properties = (JSONObject) obj.get("properties");


                String id = obj.get("id").toString();
                String nickname = properties.get("nickname").toString();

                result.put("id", id);
                result.put("nickname", nickname);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getKakaoToken(String code, String redirectUrl) throws org.json.simple.parser.ParseException {
        // 인가코드로 토큰받기
        String host = "https://kauth.kakao.com/oauth/token";

        // https://withseungryu.tistory.com/116 : RestTemplate 참고할 블로그
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // restTemplate 에러 메세지 확인 설정

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("grant_type", "authorization_code");
        param.add("client_id", clientId);
        param.add("redirect_uri", redirectUrl); //로컬, 개발, 운영 서버 테스트에서 계속 변경할 수 있게 Redirect Url 파라미터로 받아서 적용
        param.add("code", code);
        param.add("client_secret", client_secret);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(param, headers);
        ResponseEntity<String> res = rt.exchange(host,
                HttpMethod.POST,
                req,
                String.class);

        JSONParser jsonParser = new JSONParser();
        org.json.simple.JSONObject parse = (org.json.simple.JSONObject) jsonParser.parse(res.getBody());

        return (String) parse.get("access_token");
    }

    public String getAgreementInfo(String access_token) throws IOException {
        StringBuilder result = new StringBuilder();
        String host = "https://kapi.kakao.com/v2/user/scopes";

        URL url = new URL(host);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Bearer "+access_token);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            String line;
            while((line=br.readLine())!=null)
            {
                result.append(line);
            }

            int responseCode = urlConnection.getResponseCode();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}