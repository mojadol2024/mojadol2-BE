package com.gnu.pbl2.user.service;

import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.dto.UserResponseDto;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.entity.enums.Tier;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public String signup(UserRequestDto userRequestDto) {

        try {
            User user = userRequestDto.toEntity(userRequestDto);
            user.setTier(Tier.FREE);

            User response = userRepository.save(user);

            return response.getUsername();
        }catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMostSpecificCause().getMessage();
            if (errorMessage.contains("user_login_id_unique")) {
                throw new UserHandler(ErrorStatus.USER_ID_IN_USE);
            } else if (errorMessage.contains("nickname_unique")) {
                throw new UserHandler(ErrorStatus.USER_NICKNAME_IN_USE);
            } else if (errorMessage.contains("email_unique")) {
                throw new UserHandler(ErrorStatus.USER_EMAIL_IN_USE);
            } else if (errorMessage.contains("phone_number_unique")) {
                throw new UserHandler((ErrorStatus.USER_PHONENUMBER_IN_USE));
            } else {
                throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e) {
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
