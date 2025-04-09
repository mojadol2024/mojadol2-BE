package com.gnu.pbl2.user.controller;

import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/mypage")
public class MyPageController {

    private final UserService userService;


    @PostMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestHeader("Authorization") String accessToken,
                                           @RequestBody UserRequestDto userRequestDto) {

        String response = userService.checkPassword(userRequestDto, accessToken);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String accessToken,
                                           @RequestBody UserRequestDto userRequestDto) {

        String response = userService.updateProfile(userRequestDto, accessToken);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }
}
