package com.strongcom.doormate.controller;

import com.strongcom.doormate.service.impl.AlarmService;
import com.strongcom.doormate.service.impl.FirebaseCloudMessageService;
import com.strongcom.doormate.service.impl.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FCMController {


    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final ReminderService reminderService;
    private final AlarmService alarmService;

//    @PostMapping("/api/fcm")
//    public ResponseEntity pushMessage(@RequestBody AlarmDto alarmDto) throws IOException {
//        List<Long> reminderList = firebaseCloudMessageService.showAlarm(alarmDto);
//        for (Long reminderId : reminderList
//        ) {
//            RequestDTO requestDTO = firebaseCloudMessageService.reminderToFcmMessage(reminderId);
//            firebaseCloudMessageService.sendMessageTo(
//                    requestDTO.getTargetToken(),
//                    requestDTO.getTitle(),
//                    requestDTO.getBody());
//        }
//        return ResponseEntity.ok().build();
//    }
}
