package com.gnu.pbl2.user.dto;

import com.gnu.pbl2.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    private String email;
    private String phoneNumber;
    private String userName;
    private String userPw;
    private String nickname;
    private String userLoginId;

    public User toEntity(UserRequestDto userRequestDto) {
        User user = new User();
        user.setUserPw(userRequestDto.getUserPw());
        user.setUserName(userRequestDto.getUserName());
        user.setEmail(userRequestDto.getEmail());
        user.setNickname(userRequestDto.getNickname());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setUserLoginId(userRequestDto.getUserLoginId());
        return user;
    }

}
