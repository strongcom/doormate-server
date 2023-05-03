package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.Reminder;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FirebaseCloudMessageServiceTest {

    @Autowired
    private FirebaseCloudMessageService firebaseCloudMessageService;

    @Test
    @Transactional
    public void 현재나간_유저_알림내역_조회() throws Exception {
        // given


        // when
        List<Reminder> byNow = firebaseCloudMessageService.findByNow("user", LocalDateTime.now());

        // then
        for (Reminder reminder : byNow
        ) {
            String title = reminder.getTitle();
            System.out.println("title = " + title);
        }


    }
}