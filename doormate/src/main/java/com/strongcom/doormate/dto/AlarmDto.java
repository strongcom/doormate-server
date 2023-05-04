package com.strongcom.doormate.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlarmDto {
    private String userName;
    private LocalDateTime today;
}
