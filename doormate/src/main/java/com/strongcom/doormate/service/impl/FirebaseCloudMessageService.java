package com.strongcom.doormate.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.strongcom.doormate.domain.Alarm;
import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.FcmMessage;
import com.strongcom.doormate.dto.RequestDTO;
import com.strongcom.doormate.exception.NotFoundAlarmException;
import com.strongcom.doormate.exception.NotFoundUserException;
import com.strongcom.doormate.repository.AlarmRepository;
import com.strongcom.doormate.repository.ReminderRepository;
import com.strongcom.doormate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Transactional
    public List<Reminder> findByNow(String userName, LocalDateTime now) {
        // 해당 유저 아이디 값이 들어온 것을 확인
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new NotFoundUserException(NOT_FIND_USER_MESSAGE));
        List<Alarm> alarms = alarmRepository
                .findAllByNoticeDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual
                        (now.toLocalDate(), now.toLocalTime(), now.toLocalTime());
        List<Reminder> reminders = new ArrayList<>();
        for (Alarm alarm : alarms
        ) {
            if (alarm.getReminder().getUser().equals(user)) {
                reminders.add(alarm.getReminder());
                alarmRepository.deleteById(alarm.getId());
            }
//            alarmRepository.deleteById(alarm.getId());
        }
        if (reminders.size() == 0) {
            throw new NotFoundAlarmException("현재 예정된 알림이 없습니다.");
        }
        return reminders;
    }

    @Transactional
    public RequestDTO reminderToFcmMessage(Long id) {
        // 알림 서비스에 넘어온 리마인더 id 값을 받아 리마인더 조회후, requestDto에 담아서 넘기기
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new NotFoundAlarmException(NOT_FIND_REMINDER_MESSAGE));
        return RequestDTO.builder()
                .title(reminder.getTitle())
                .body(reminder.getContent())
                .build();
    }

}
