package com.gnu.pbl2.user.controller;

import com.gnu.pbl2.response.ApiResponse;
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
    public ResponseEntity<?> signup(@RequestBody UserRequestDto userRequestDto) {
        String response = userService.signup(userRequestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess(response + "회원가입 성공"));
    }

    //로그인
    @PostMapping("/signIn")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto userRequestDto) {
        //인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequestDto.getUserLoginId(), userRequestDto.getUserPw())
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        tokenService.saveToken(userDetails.getUsername(), refreshToken, 120, TimeUnit.MINUTES);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(new UserResponseDto(userDetails.getUsername(), accessToken, refreshToken));
    }

    //로그아웃
    @PostMapping("/signOut")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        tokenService.deleteToken(username);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("로그아웃 완료");
    }
}
