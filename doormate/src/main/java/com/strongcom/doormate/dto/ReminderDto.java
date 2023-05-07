package com.strongcom.doormate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.strongcom.doormate.domain.RepetitionPeriod;
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
    private LocalDate startDate = LocalDate.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime = LocalTime.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime = LocalTime.of(23, 59);

    private RepetitionPeriod repetitionPeriod = RepetitionPeriod.BASIC;  // 반복주기(매일, 매주, 매월, 매년)

    private String repetitionDay;   // 반복 주기(요일별)

}
