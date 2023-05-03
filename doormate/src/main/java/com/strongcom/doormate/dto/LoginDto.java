package com.strongcom.doormate.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {


    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    @Size
    private String password;

}
