package com.strongcom.doormate.controller;

import com.strongcom.doormate.domain.Message;
import com.strongcom.doormate.dto.AlarmDto;
import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.service.impl.AlarmService;
import com.strongcom.doormate.service.impl.FirebaseCloudMessageService;
import com.strongcom.doormate.service.impl.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class FCMController {


    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final ReminderService reminderService;
    private final AlarmService alarmService;


    @PostMapping("/api/fcm")
    public ResponseEntity<Message> pushMessage(@RequestBody AlarmDto alarmDto) throws IOException {
        List<Reminder> reminders = firebaseCloudMessageService.findByNow(alarmDto.getUserName(), alarmDto.getToday());
        for (Reminder reminder : reminders
        ) {
            log.info(reminder.getUser().getUsername() + " "
                    +reminder.getTitle() + " " + reminder.getContent());

            firebaseCloudMessageService.sendMessageTo(
                    reminder.getUser().getTargetToken(),
                    reminder.getTitle(),
                    reminder.getContent());
        }
        return ResponseEntity.ok().body(new Message("푸시 알림 전송 완료"));
    }
}
