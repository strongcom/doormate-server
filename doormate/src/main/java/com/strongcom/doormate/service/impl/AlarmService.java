package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.Alarm;
import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.ReminderRespDto;
import com.strongcom.doormate.exception.NotFoundReminderException;
import com.strongcom.doormate.exception.NotFoundUserException;
import com.strongcom.doormate.repository.AlarmRepository;
import com.strongcom.doormate.repository.ReminderRepository;
import com.strongcom.doormate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final AlarmRepository alarmRepository;

    private static final String NOT_FIND_USER_MESSAGE = "해당 회원의 알람 리스트가 존재하지 않습니다.";
    private static final String NOT_FIND_REMINDER_MESSAGE = "해당 리마인더가 존재하지 않습니다.";
    private static final String SUCCESS_SAVED_ALARM_MESSAGE = "반복 설정 등록 완료";

    // === Alarm 테이블에 저장 === //
    @Transactional
    public String saveAlarm(Long reminder_id) {
        Reminder reminder = reminderRepository.findById(reminder_id)
                .orElseThrow(() -> new NotFoundReminderException(NOT_FIND_REMINDER_MESSAGE));
        List<LocalDate> dates = reminder.findByDate();
        for (LocalDate date : dates) {
            Alarm alarm = Alarm.createAlarm(date,reminder);
            reminder.addAlarm(alarm);
//            alarmRepository.save(alarm);
        }
        return SUCCESS_SAVED_ALARM_MESSAGE;
    }

    @Transactional
    public void deleteOneAlarm(Alarm alarm) {
        if (alarm.getReminder().getAlarms().size() == 1) {
            reminderRepository.deleteById(alarm.getReminder().getReminderId());
        }
        alarmRepository.deleteById(alarm.getId());
    }

    @Transactional
    public List<ReminderRespDto> findTodayAlarm(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 유저입니다."));
        System.out.println("LocalDate.now() = " + LocalTime.now());

        List<Alarm> todayAlarmList = alarmRepository.findAllToday(LocalDate.now());
        List<ReminderRespDto> reminders = new ArrayList<>();
        for (Alarm alarm : todayAlarmList
        ) {
            if (alarm.getReminder().getUser().equals(user)) {
                reminders.add(alarm.getReminder().setReminderRespDto());
            }
        }
        return reminders;
    }


}
