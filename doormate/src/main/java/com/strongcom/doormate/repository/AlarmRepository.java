package com.strongcom.doormate.repository;

import com.strongcom.doormate.domain.Alarm;
import com.strongcom.doormate.domain.Reminder;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    void deleteAllByReminder(Reminder reminder);

    List<Alarm> findAllByNoticeDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(LocalDate noticeDate, LocalTime now1, LocalTime now2);

    @Query(value = "select alarm from Alarm alarm where alarm.noticeDate=:today")
    List<Alarm> findAllToday(@Param("today") LocalDate today);

    @Transactional
    void deleteAllByNoticeDateLessThanEqual(LocalDate today);

}
