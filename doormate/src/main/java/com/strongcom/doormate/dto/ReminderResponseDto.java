package com.strongcom.doormate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.strongcom.doormate.domain.RepetitionPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class ReminderResponseDto {
    private Long id;

    private String title;

    private String content;

    private String subTitle;

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

}
