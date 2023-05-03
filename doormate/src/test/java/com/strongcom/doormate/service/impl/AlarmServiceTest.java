package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.RepetitionPeriod;
import com.strongcom.doormate.dto.ReminderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootTest
class AlarmServiceTest {

    @Autowired
    private ReminderService reminderService;
    @Autowired
    private AlarmService alarmService;

    @Test
    void 반복_알람_저장() {
        // given
        ReminderDto reminderDto = ReminderDto.builder()
                .title("매달 반복 내용 저장")
                .content("매달 반복")
                .startDate(LocalDate.of(2023, 5, 3))
                .endDate(LocalDate.of(2023, 5, 3))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(18, 0))
                .repetitionPeriod(RepetitionPeriod.DAILY)
                .repetitionDay("")
                .build();


        Long reminderId = reminderService.saveReminder(1L, reminderDto);

        // when
        String s = alarmService.saveAlarm(reminderId);
        // then
        System.out.println(s);
    }

    @Test
    void 일회성_알림저장() {
        // given
        ReminderDto reminderDto = ReminderDto.builder()
                .title("내생일")
                .content("신난다")
                .startDate(LocalDate.of(2023,6,24))
                .endDate(LocalDate.of(2023,6,24))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(18, 0))
                .repetitionPeriod(RepetitionPeriod.BASIC)
                .repetitionDay("")
                .build();


        Long reminderId = reminderService.saveReminder(1L, reminderDto);

        // when
        String s = alarmService.saveAlarm(reminderId);
        // then
        System.out.println(s);
    }

    @Test
    public void 리마인더아이디에_해당하는_모든알림삭제() throws Exception {
        // given


        // when
        alarmService.deleteAlarm(7L);

        // then


    }
}