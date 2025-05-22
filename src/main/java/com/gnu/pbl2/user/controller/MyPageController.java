package com.gnu.pbl2.user.controller;

import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.user.dto.UserProfileDto;
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

    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(@RequestHeader("Authorization") String accessToken,
                                           @RequestBody UserRequestDto userRequestDto) {
        log.info("비밀번호 확인 요청 - userLoginId={}", userRequestDto.getUserLoginId());

        boolean response = userService.checkPassword(userRequestDto, accessToken);
        String message = response ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다.";
        return ResponseEntity.ok().body(ApiResponse.onSuccess(message));
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String accessToken,
                                           @RequestBody UserRequestDto userRequestDto) {
        log.info("프로필 수정 요청 - userLoginId={}", userRequestDto.getUserLoginId());

        String response = userService.updateProfile(userRequestDto, accessToken);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

    @DeleteMapping("/resign")
    public ResponseEntity<?> resignUser(@RequestHeader("Authorization") String accessToken) {
        log.info("회원 탈퇴 요청");

        String response = userService.resignUser(accessToken);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestHeader("Authorization") String accessToken) {
        log.info("회원 정보 요청");

        UserProfileDto response = userService.profile(accessToken);
        return ResponseEntity.ok().body(ApiResponse.onSuccess(response));
    }
}
