package com.gnu.pbl2.user.controller;

import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.response.code.BaseErrorCode;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.dto.UserResponseDto;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.service.TokenService;
import com.gnu.pbl2.user.service.UserService;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mojadol/api/v1/auth")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;


    //회원가입
    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody UserRequestDto userRequestDto) {
        String response = userService.signup(userRequestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess(response + "회원가입 성공"));
    }

    //로그인
    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody UserRequestDto userRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequestDto.getUserLoginId(), userRequestDto.getUserPw())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            // 리프레시 토큰 저장 (Redis 또는 DB)
            tokenService.saveToken(userDetails.getUsername(), refreshToken, 120, TimeUnit.MINUTES);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, accessToken);
            headers.set("RefreshToken", refreshToken);

            return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess("로그인 성공"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(ErrorStatus.USER_NOT_FOUND.getHttpStatus())
                    .body(ApiResponse.onFailure(ErrorStatus.USER_NOT_FOUND.getCode(), ErrorStatus.USER_NOT_FOUND.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR.getCode(), ErrorStatus.INTERNAL_SERVER_ERROR.getMessage()));
        }
    }


    //로그아웃
    @PostMapping("/signOut")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        tokenService.deleteToken(username);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.onSuccess("로그아웃 완료"));
    }
}
