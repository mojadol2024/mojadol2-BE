package com.gnu.pbl2.user.dto;

import com.gnu.pbl2.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private String userName;
    private String accessToken;
    private String refreshToken;
}
