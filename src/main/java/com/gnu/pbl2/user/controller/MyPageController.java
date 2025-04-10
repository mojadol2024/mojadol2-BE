package com.gnu.pbl2.user.controller;

import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mojadol/api/v1/mypage")
@Slf4j
public class MyPageController {

    private final UserService userService;

    @PostMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestHeader("Authorization") String accessToken,
                                           @RequestBody UserRequestDto userRequestDto) {
        log.info("[MyPageController] [POST] checkPassword 요청 진입 - userLoginId={}", userRequestDto.getUserLoginId());

        boolean response = userService.checkPassword(userRequestDto, accessToken);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다."));
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String accessToken,
                                           @RequestBody UserRequestDto userRequestDto) {
        log.info("[MyPageController] [POST] updateProfile 요청 진입 - userLoginId={}", userRequestDto.getUserLoginId());

        String response = userService.updateProfile(userRequestDto, accessToken);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

    @PostMapping("/resignUser")
    public ResponseEntity<?> resignUser(@RequestHeader("Authorization") String accessToken) {
        log.info("[MyPageController] [POST] resignUser 요청 진입");

        String response = userService.resignUser(accessToken);

        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }
}
