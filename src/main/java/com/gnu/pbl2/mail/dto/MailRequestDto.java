package com.gnu.pbl2.mail.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequestDto {
    private String code;
    private String email;
    private String userLoginId;
}
