package com.strongcom.doormate.controller;

import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.ReminderDto;
import com.strongcom.doormate.service.impl.AlarmService;
import com.strongcom.doormate.service.impl.ReminderService;
import com.strongcom.doormate.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


    @PostMapping()
    public String create(@RequestBody ReminderDto reminderRequestDto) {
        Long savedReminderId = reminderService.saveReminder(getUser().getUserId(), reminderRequestDto);
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

    @GetMapping("/today")
    public List<Reminder> findToday() {
        return alarmService.findTodayAlarm(getUser().getUserId());
    }

    @GetMapping()
    public List<Reminder> findAll() {
        return reminderService.findAllReminder(getUser().getUserId());
    }

    private static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
