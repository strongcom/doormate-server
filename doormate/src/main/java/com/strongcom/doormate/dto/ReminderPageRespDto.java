package com.strongcom.doormate.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReminderPageRespDto {

    private Long reminderId;
    private String title;
    private String content;

    @Builder
    public ReminderPageRespDto(Long reminderId, String title, String content) {
        this.reminderId = reminderId;
        this.title = title;
        this.content = content;
    }
}
