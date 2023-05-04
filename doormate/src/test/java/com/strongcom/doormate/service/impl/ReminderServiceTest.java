package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.RepetitionPeriod;
import com.strongcom.doormate.dto.ReminderDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
class ReminderServiceTest {

    @Autowired
    private ReminderService reminderService;


    @Test
    void 리마인더저장() {
        // given
        ReminderDto reminderDto = ReminderDto.builder()
                .title("subtitle test")
                .content("해야할일")
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2023, 5, 3))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .repetitionPeriod(RepetitionPeriod.DAILY)
                .repetitionDay("MON TUE WED")
                .build();



        // when
        Long reminderId = reminderService.saveReminder(1L, reminderDto);

        // then
    }

    @Test
    void 리마인더한개만저장() {
        // given
        ReminderDto reminderDto = ReminderDto.builder()
                .title("내생일")
                .content("신난다")
                .startDate(LocalDate.of(2023,6,24))
                .endDate(LocalDate.of(2023,6,25))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(18, 0))
                .repetitionPeriod(RepetitionPeriod.BASIC)
                .repetitionDay("")
                .build();


        // when
        Long reminderId = reminderService.saveReminder(1L, reminderDto);

        // then
    }
    @Test
    @Transactional
    public void 리마인더_조회() throws Exception {
        // given


        // when
        List<Reminder> allReminder = reminderService.findAllReminder(1L);


        // then
        for (Reminder reminder : allReminder
        ) {
            System.out.println(reminder.getTitle());
        }


    }

    @Test
    void updateReminder() {
        // given
        ReminderDto reminderDto = ReminderDto.builder()
                .title("제목")
                .content("해야할일")
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2023, 5, 30))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .repetitionPeriod(RepetitionPeriod.WEEKLY)
                .repetitionDay("MON TUE WED")
                .build();

        // when
        Long reminder = reminderService.updateReminder(2L, reminderDto);

        // then
        Assertions.assertThat(reminder).isEqualTo(2);
    }

    @Test
    void deleteReminder() {
    }
}