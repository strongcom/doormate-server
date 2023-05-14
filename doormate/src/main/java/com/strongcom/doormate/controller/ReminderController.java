package com.strongcom.doormate.controller;

import com.strongcom.doormate.dto.ReminderDto;
import com.strongcom.doormate.dto.ReminderPageRespDto;
import com.strongcom.doormate.dto.ReminderRespDto;
import com.strongcom.doormate.dto.ReminderResponseDto;
import com.strongcom.doormate.service.impl.AlarmService;
import com.strongcom.doormate.service.impl.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;
    private final AlarmService alarmService;
    private static final String CREATE_REMINDER_MESSAGE = "리마인더 등록 완료";
    private static final String UPDATE_REMINDER_MESSAGE = "알람 수정 완료";
    private static final String DELETE_REMINDER_MESSAGE = "리마인더 삭제 완료";


    @PostMapping()
    public String create(@RequestBody ReminderDto reminderRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savedReminderId = reminderService.saveReminder(user.getUsername(), reminderRequestDto);
        alarmService.saveAlarm(savedReminderId);
        return CREATE_REMINDER_MESSAGE;
    }


    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long reminderId, @RequestBody ReminderDto reminderDto) {
        Long savedReminder = reminderService.updateReminder(reminderId, reminderDto);
//        alarmService.deleteAlarm(reminderId);
        alarmService.saveAlarm(savedReminder);
        return UPDATE_REMINDER_MESSAGE;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long reminderId) {
//        alarmService.deleteAlarm(reminderId);
        reminderService.deleteReminder(reminderId);
        return DELETE_REMINDER_MESSAGE;
    }

    @GetMapping("/today")
    public List<ReminderResponseDto> findToday() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return alarmService.findTodayAlarm(user.getUsername());
    }

    @GetMapping("/{id}")
    public ReminderRespDto findOne(@PathVariable("id") Long reminderId) {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return reminderService.findOneReminder(reminderId);
    }

    @GetMapping("/{id}")
    public List<ReminderPageRespDto> findAll(@PathVariable Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return reminderService.findAllReminder(user.getUsername());
    }

}
