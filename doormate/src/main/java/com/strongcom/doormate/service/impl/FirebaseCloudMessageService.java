package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.dto.FcmMessage;
import com.strongcom.doormate.repository.AlarmRepository;
import com.strongcom.doormate.repository.ReminderRepository;
import com.strongcom.doormate.repository.UserRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {
    private final ReminderRepository reminderRepository;
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/strongcom/messages:send";
    private final ObjectMapper objectMapper;

    private static final String NOT_FIND_REMINDER_MESSAGE = "현재 알림이 예정된 리마인더가 존재하지 않습니다.";
    private static final String NOT_FIND_USER_MESSAGE = "해당 회원의 알람 리스트가 존재하지 않습니다.";

    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post((RequestBody) requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

//    public List<Long> showAlarm(AlarmDto alarmDto) {
//        // 해당 유저 아이디 값이 들어온 것을 확인
//        User user = userRepository.findByUsername(alarmDto.getUserName()).orElseThrow(() -> new NotFoundException(NOT_FIND_USER_MESSAGE));
//        List<Alarm> alarmList = alarmRepository.findAllByUser(user);  // 현관문을 나간 사용자의 알람 리스트 전체 조회
//        List<Long> reminderList = new ArrayList<>();
//
//        // 지정된 날짜정보 받아오기
//        // 현재 시간 정보를 받아옴
//        LocalDate localDate = alarmDto.getCheckoutTime().toLocalDate();
//        LocalTime localTime = alarmDto.getCheckoutTime().toLocalTime();
//
//        // 현재시간과 알림 테이블에 있는 목록을 조회하여 필터링
//        // 현재 시간에 해당되는 알림 호출
//        for (Alarm alarm : alarmList
//        ) {
//            if (alarm.getNoticeDate() == localDate)
//                if (alarm.getStartTime().isAfter(localTime) && alarm.getEndTime().isBefore(localTime))
//                    reminderList.add(alarm.getReminder().getReminderId());
//        }
//        return reminderList;
//        // 이후 알림 리스트에 해당하는 리마인더 조회 쿼리를 만들어 조회하게 만들기
//    }
//
//    public RequestDTO reminderToFcmMessage(Long id) {
//        // 알림 서비스에 넘어온 리마인더 id 값을 받아 리마인더 조회후, requestDto에 담아서 넘기기
//        Reminder reminder = reminderRepository.findById(id).orElseThrow(() -> new RuntimeException(NOT_FIND_REMINDER_MESSAGE));
//        return RequestDTO.builder()
//                .title(reminder.getTitle())
//                .body(reminder.getContent())
//                .build();
//    }

}
