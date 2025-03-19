package com.gnu.pbl2.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequestDto {
    private String refreshToken;
    private String accessToken;
}
