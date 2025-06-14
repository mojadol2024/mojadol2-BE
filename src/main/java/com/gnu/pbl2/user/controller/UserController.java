package com.gnu.pbl2.user.controller;

import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.TokenRequestDto;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.entity.CustomUserDetails;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.service.TokenService;
import com.gnu.pbl2.user.service.UserService;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mojadol/api/v1")
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    @PostMapping("/users/sign-up")
    public ResponseEntity<ApiResponse<String>> signUp(@RequestBody UserRequestDto userRequestDto) {
        log.info("[POST] 회원가입 요청");
        try {
            String response = userService.signUp(userRequestDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(response + "회원가입 성공"));
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody UserRequestDto userRequestDto) {
        log.info("[POST] 로그인 요청");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequestDto.getUserLoginId(), userRequestDto.getUserPw())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            String accessToken = jwtUtil.generateAccessToken(userId);
            String refreshToken = jwtUtil.generateRefreshToken(userId);

            tokenService.saveToken(userId, refreshToken, 120, TimeUnit.MINUTES);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, accessToken);
            headers.set("RefreshToken", refreshToken);

            return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess("로그인 성공"));
        } catch (BadCredentialsException e) {
            throw new UserHandler(ErrorStatus.USER_BAD_CREDENTIALS);
        } catch (UsernameNotFoundException e) {
            throw new UserHandler(ErrorStatus.USER_NOT_FOUND);
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String accessToken) {
        log.info("[POST] 로그아웃 요청");
        try {
            tokenService.deleteToken(accessToken);
            return ResponseEntity.ok(ApiResponse.onSuccess("로그아웃 완료"));
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.ACCESS_TOKEN_EXPIRED);
        }
    }

    // 리프레시 토큰
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRequestDto request) {
        log.info("[POST] 리프레시 요청");
        String refreshToken = request.getRefreshToken();
        Long userId = jwtUtil.extractUserId(refreshToken);

        String storedRefreshToken = tokenService.getToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new UserHandler(ErrorStatus.REFRESH_TOKEN_NOT_MATCH);
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, newAccessToken);

        return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess("리프레시 성공"));
    }

    // 회원가입 중복체크
    @GetMapping("/users/check")
    public ResponseEntity<?> signUpCheck(@RequestParam(required = false) String userLoginId,
                                         @RequestParam(required = false) String email,
                                         @RequestParam(required = false) String nickname,
                                         @RequestParam(required = false) String phoneNumber
                                         ) {
        log.info("[GET] 회원가입 중복체크 요청");
        try {
            UserRequestDto userRequestDto = new UserRequestDto();
            userRequestDto.setUserLoginId(userLoginId);
            userRequestDto.setNickname(nickname);
            userRequestDto.setEmail(email);
            userRequestDto.setPhoneNumber(phoneNumber);

            Optional<User> user = userService.signUpCheck(userRequestDto);

            if (user.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.onSuccess("중복되는 데이터가 없습니다."));
            } else {
                return ResponseEntity.ok(ApiResponse.onSuccess("데이터가 중복됩니다."));
            }

        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
