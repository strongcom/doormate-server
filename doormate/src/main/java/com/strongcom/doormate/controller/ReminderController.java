package com.strongcom.doormate.controller;

import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.dto.ReminderDto;
import com.strongcom.doormate.service.impl.AlarmService;
import com.strongcom.doormate.service.impl.ReminderService;
import com.strongcom.doormate.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;
    private final AlarmService alarmService;
    private final UserServiceImpl userService;
    private static final String CREATE_REMINDER_MESSAGE = "리마인더 등록 완료";
    private static final String UPDATE_REMINDER_MESSAGE = "알람 수정 완료";
    private static final String DELETE_REMINDER_MESSAGE = "리마인더 삭제 완료";


    @PostMapping("/{id}")
    public String create(@PathVariable("id") Long userId, @RequestBody ReminderDto reminderRequestDto) {
        Long savedReminderId = reminderService.saveReminder(userId, reminderRequestDto);
        alarmService.saveAlarm(savedReminderId);
        return CREATE_REMINDER_MESSAGE;
    }


    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long reminderId, @RequestBody ReminderDto reminderDto) {
        Long savedReminder = reminderService.updateReminder(reminderId, reminderDto);
        alarmService.deleteAlarm(reminderId);
        alarmService.saveAlarm(savedReminder);
        return UPDATE_REMINDER_MESSAGE;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long reminderId) {
        alarmService.deleteAlarm(reminderId);
        reminderService.deleteReminder(reminderId);
        return DELETE_REMINDER_MESSAGE;
    }

    @GetMapping("/today/{id}")
    public List<Reminder> findToday(@PathVariable("id") Long userId) {
        return alarmService.findTodayAlarm(userId);
    }

    @GetMapping("/{id}")
    public List<Reminder> findAll(@PathVariable("id") Long id) {
        return reminderService.findAllReminder(id);
    }


}
