package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.Alarm;
import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.exception.NotFoundReminderException;
import com.strongcom.doormate.exception.NotFoundUserException;
import com.strongcom.doormate.repository.AlarmRepository;
import com.strongcom.doormate.repository.ReminderRepository;
import com.strongcom.doormate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
            System.out.println(date);
            Alarm alarm = Alarm.createAlarm(date,reminder);
            alarmRepository.save(alarm);
        }
        return SUCCESS_SAVED_ALARM_MESSAGE;
    }

    @Transactional
    public void deleteAlarm(Long reminder_id) {
        Reminder reminder = reminderRepository.findById(reminder_id)
                .orElseThrow(() -> new NotFoundReminderException("해당 리마인더가 존재하지 않습니다."));
        alarmRepository.deleteAllByReminder(reminder);
    }

    @Transactional
    public List<Reminder> findTodayAlarm(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 유저입니다."));
        List<Alarm> todayAlarmList = alarmRepository.findAllToday(LocalDate.now());
        List<Reminder> reminders = new ArrayList<>();
        for (Alarm alarm : todayAlarmList
        ) {
            if (alarm.getReminder().getUser().equals(user)) {
                reminders.add(alarm.getReminder());
            }
        }
        return reminders;
    }
}
