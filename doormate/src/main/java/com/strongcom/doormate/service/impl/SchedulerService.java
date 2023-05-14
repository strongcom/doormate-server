package com.strongcom.doormate.service.impl;

import com.strongcom.doormate.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final AlarmRepository alarmRepository;
    @Scheduled(cron = "59 59 23 * * *", zone = "Asia/Seoul")
    public void run() {
        alarmRepository.deleteAllByNoticeDateLessThanEqual(LocalDate.now());
        System.out.println("금일 미알림 내역 삭제");
    }
}
