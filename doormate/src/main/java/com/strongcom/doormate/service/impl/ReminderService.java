package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.ReminderDto;
import com.strongcom.doormate.exception.NotFoundReminderException;
import com.strongcom.doormate.exception.NotFoundUserException;
import com.strongcom.doormate.repository.ReminderRepository;
import com.strongcom.doormate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;

    private static final String SAVE_REMINDER_SUCCESS_MESSAGE = "리마인더 등록 완료";
    private static final String UPDATE_SUCCESS_MESSAGE = "리마인더 수정 완료";
    private static final String ERROR_NOT_EXISTS_REMINDER_MESSAGE = "등록된 리마인더가 존재하지 않습니다.";
    private static final String NOT_CORRECT_DATE_MESSAGE = "설정한 날짜의 범위를 다시 확인하세요.";
    private static final String DELETE_SUCCESS_MESSAGE = "리마인더 삭제 완료";

    @Transactional
    public Long saveReminder(String username, ReminderDto reminderDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundUserException("가입되지 않은 유저입니다. 회원가입 후 리마인더를 등록해주세요."));
        Reminder reminder = Reminder.createReminder(user, reminderDto);
        Reminder savedReminder = reminderRepository.save(reminder);
        return savedReminder.getReminderId();
    }

    public List<Reminder> findAllReminder(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundUserException("해당 유저는 존재하지 않습니다."));
        return reminderRepository.findAllByUser(user);
    }

    @Transactional
    public Long updateReminder(Long reminderId, ReminderDto reminderDto) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new NotFoundReminderException("해당 리마인더는 존재하지 않습니다."));
        reminder.setReminder(reminderDto);
        //Reminder savedReminder = reminderRepository.save(reminder);
        return reminder.getReminderId();
    }

    public void deleteReminder(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }



}
