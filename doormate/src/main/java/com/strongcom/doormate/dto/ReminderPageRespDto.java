package com.strongcom.doormate.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderPageRespDto {

    private Long reminderId;
    private String title;
    private String subTitle;

}
