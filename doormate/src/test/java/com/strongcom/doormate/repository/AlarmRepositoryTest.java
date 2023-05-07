package com.strongcom.doormate.repository;

import com.strongcom.doormate.domain.Alarm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class AlarmRepositoryTest {
    @Autowired
    private AlarmRepository alarmRepository;
    @Test
    @Transactional
    public void 현재시간과_비교하여_알람추출() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        List<Alarm> alarms = alarmRepository.findAllByNoticeDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(now.toLocalDate(), now.toLocalTime().minusMinutes(10), now.toLocalTime());

        // then
        for (Alarm alarm : alarms
        ) {
            String title = alarm.getReminder().getTitle();
            System.out.println("title = " + title);
        }

    }

    @Test
    public void 오늘_알람리스트_전체삭제() throws Exception {

        // given
        LocalDate today = LocalDate.now();

        // when
        alarmRepository.deleteAllByNoticeDate(today);

        // then


    }

}