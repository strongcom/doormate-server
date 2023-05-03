package com.strongcom.doormate.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO {
    private String targetToken;
    private String title;
    private String body;

    @Builder
    public RequestDTO(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
