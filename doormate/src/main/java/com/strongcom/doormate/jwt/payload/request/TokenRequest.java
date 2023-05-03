package com.strongcom.doormate.jwt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {

    @NotNull
    private String accessToken;

    @NotNull
    private String refreshToken;

}
