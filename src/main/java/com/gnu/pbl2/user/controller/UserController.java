package com.gnu.pbl2.user.controller;

import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.response.code.BaseErrorCode;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.TokenRequestDto;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.dto.UserResponseDto;
import com.gnu.pbl2.user.entity.CustomUserDetails;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.service.TokenService;
import com.gnu.pbl2.user.service.UserService;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
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
        try{
            String response = userService.signUp(userRequestDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(response + "회원가입 성공"));
        } catch (HttpClientErrorException.BadRequest e){
            // 데이터가 잘못됨
            throw new UserHandler(ErrorStatus.USER_BAD_REQUEST);
        } catch (Exception e) {
            // 그 외 에러
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //로그인
    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody UserRequestDto userRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequestDto.getUserLoginId(), userRequestDto.getUserPw())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId(); // userId 가져오기
            String accessToken = jwtUtil.generateAccessToken(userId);
            String refreshToken = jwtUtil.generateRefreshToken(userId);

            // 리프레시 토큰 저장 (Redis 또는 DB)
            tokenService.saveToken(userId, refreshToken, 120, TimeUnit.MINUTES);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, accessToken);
            headers.set("RefreshToken", refreshToken);

            return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess("로그인 성공"));
        } catch (BadCredentialsException e) {
            // 아이디 비밀번호 불일치
            throw new UserHandler(ErrorStatus.USER_BAD_CREDENTIALS);
        } catch (UsernameNotFoundException e) {
            // 유저가 없음
            throw new UserHandler(ErrorStatus.USER_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그아웃
    @PostMapping("/signOut")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String accessToken) {
        try {
            tokenService.deleteToken(accessToken);
            return ResponseEntity.ok(ApiResponse.onSuccess("로그아웃 완료"));
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.ACCESS_TOKEN_EXPIRED);
        }
    }

    // 리프레시
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRequestDto request) {
        String refreshToken = request.getRefreshToken();
        Long userId = jwtUtil.extractUserId(refreshToken);

        // Redis에서 저장된 리프레시 토큰 가져오기
        String storedRefreshToken = tokenService.getToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new UserHandler(ErrorStatus.REFRESH_TOKEN_NOT_MATCH);
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, newAccessToken);

        return ResponseEntity.ok().headers(headers).body(ApiResponse.onSuccess("리프레시 성공"));
    }

    // 아이디 유니크 체크
    // 메일 유니크 체크
    // 닉네임 유니크 체크
    @PostMapping("/signUpCheck")
    public ResponseEntity<?> signUpCheck(@RequestBody UserRequestDto userRequestDto) {
        try {

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
