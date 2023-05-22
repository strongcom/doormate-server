package com.strongcom.doormate.controller;

import com.strongcom.doormate.domain.Message;
import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.ReminderDto;
import com.strongcom.doormate.dto.ReminderPageRespDto;
import com.strongcom.doormate.dto.ReminderRespDto;
import com.strongcom.doormate.kakao.service.KakaoService;
import com.strongcom.doormate.service.impl.AlarmService;
import com.strongcom.doormate.service.impl.ReminderService;
import com.strongcom.doormate.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;
    private final AlarmService alarmService;
    private final KakaoService kakaoService;
    private final UserServiceImpl userService;

    private static final String CREATE_REMINDER_MESSAGE = "리마인더 등록 완료";
    private static final String UPDATE_REMINDER_MESSAGE = "알람 수정 완료";
    private static final String DELETE_REMINDER_MESSAGE = "리마인더 삭제 완료";


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Message create(@RequestHeader HttpHeaders token, @RequestBody ReminderDto reminderRequestDto) throws Exception {
        User user = userService.findByKakaoUser(token);
        Long savedReminderId = reminderService.saveReminder(user.getUsername(), reminderRequestDto);
        alarmService.saveAlarm(savedReminderId);
        return new Message(CREATE_REMINDER_MESSAGE);
    }


    @PutMapping("/{id}")
    public Message update(@PathVariable("id") Long reminderId, @RequestBody ReminderDto reminderDto) {
        Long savedReminder = reminderService.updateReminder(reminderId, reminderDto);
//        alarmService.deleteAlarm(reminderId);
        alarmService.saveAlarm(savedReminder);
        return new Message(UPDATE_REMINDER_MESSAGE);
    }

    @DeleteMapping("/{id}")
    public Message delete(@PathVariable("id") Long reminderId) {
//        alarmService.deleteAlarm(reminderId);
        reminderService.deleteReminder(reminderId);
        return new Message(DELETE_REMINDER_MESSAGE);
    }

    @GetMapping("/today")
    public List<ReminderPageRespDto> findToday(@RequestHeader HttpHeaders token) throws Exception {
        User user = userService.findByKakaoUser(token);
        return alarmService.findTodayAlarm(user.getUsername());
    }

    @GetMapping("/{id}")
    public ReminderRespDto findOne(@PathVariable("id") Long reminderId) {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return reminderService.findOneReminder(reminderId);
    }

    @GetMapping()
    public List<ReminderPageRespDto> findAll(@RequestHeader HttpHeaders token) throws Exception {
        User user = userService.findByKakaoUser(token);
        return reminderService.findAllReminder(user.getUsername());
    }

}
