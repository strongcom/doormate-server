package com.strongcom.doormate.dto;

import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.RepetitionPeriod;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReminderDto {

    private String title;

    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime;

    private RepetitionPeriod repetitionPeriod;  // 반복주기(매일, 매주, 매월, 매년)

    private String repetitionDay;   // 반복 주기(요일별)



    public Reminder toReminder(ReminderDto reminderDto) {
        return Reminder.builder()
                .title(this.title)
                .content(this.content)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .repetitionPeriod(this.repetitionPeriod)
                .repetitionDay(this.repetitionDay)
                .build();
    }
}
