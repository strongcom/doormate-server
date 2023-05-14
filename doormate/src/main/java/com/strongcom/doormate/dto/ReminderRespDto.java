package com.strongcom.doormate.dto;

import com.strongcom.doormate.domain.RepetitionPeriod;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@NoArgsConstructor
public class ReminderRespDto {

    private Long reminderId;

    private String title;

    private String content;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate startDate;

    private LocalDate endDate;

    private RepetitionPeriod repetitionPeriod;

    private String repetitionDay;


    @Builder
    public ReminderRespDto(Long reminderId, String title, String content, LocalTime startTime, LocalTime endTime,
                           LocalDate startDate, LocalDate endDate,
                           RepetitionPeriod repetitionPeriod, String repetitionDay) {
        this.reminderId = reminderId;
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.repetitionPeriod = repetitionPeriod;
        this.repetitionDay = repetitionDay;
    }
}
