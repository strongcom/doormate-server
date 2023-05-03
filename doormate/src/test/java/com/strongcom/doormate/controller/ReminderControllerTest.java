package com.strongcom.doormate.controller;

import com.strongcom.doormate.dto.ReminderDto;
import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.RepetitionPeriod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
class ReminderControllerTest {

    @Autowired
    private ReminderController reminderController;


    @Test
    void create() {
        //given
        ReminderDto reminderDto = ReminderDto.builder()
                .title("시간대별로 조회가 되나 볼까 테스트3")
                .content("리마인더 등록 기능 구현")
                .startDate(LocalDate.of(2023, 5, 3))
                .endDate(LocalDate.of(2023, 6, 3))
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.of(23, 0))
                .repetitionPeriod(RepetitionPeriod.DAILY)
                .repetitionDay("MON TUE FRI")
                .build();

        //when
        String s = reminderController.create(1L, reminderDto);

        //then
        System.out.println(s);
    }

    @Test
    public void 리마인더_수정() throws Exception {
        // given
        ReminderDto reminderDto = ReminderDto.builder()
                .title("리마인더 수정 테스트")
                .content("리마인더를 수정해보자")
                .startDate(LocalDate.of(2023, 5, 3))
                .endDate(LocalDate.of(2023, 6, 1))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(18, 0))
                .repetitionPeriod(RepetitionPeriod.WEEKLY)
                .repetitionDay("MON SAT SUN")
                .build();

        // when
        String update = reminderController.update(1L, reminderDto);


        // then
        Assertions.assertThat(update).isEqualTo("알람 수정 완료");
    }

    @Test
    public void 리마인더_삭제() throws Exception {
        // given


        // when
        reminderController.delete(1L);

        // then


    }

    @Test
    public void 오늘_알림_조회() throws Exception {
        // given


        // when
        List<Reminder> today = reminderController.findToday(1L);

        // then
        for (Reminder reminder : today
        ) {
            System.out.println(reminder.getTitle());
        }


    }
}