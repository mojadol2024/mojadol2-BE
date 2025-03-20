package com.gnu.pbl2.user.service;

import com.gnu.pbl2.exception.handler.MailHandler;
import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.dto.UserResponseDto;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.entity.enums.Tier;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String signUp(UserRequestDto userRequestDto) {
        try {
            User user = userRequestDto.toEntity(userRequestDto);
            user.setTier(Tier.FREE);
            user.setUserPw(passwordEncoder.encode(user.getUserPw()));

            User response = userRepository.save(user);
            return response.getUsername();

        } catch (DataIntegrityViolationException e) {
            throw new UserHandler(ErrorStatus.USER_SQL_UNIQUE);

        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<User> signUpCheck(UserRequestDto userRequestDto) {
        try {
            if(userRequestDto.getUserLoginId() != null) {
                return userRepository.findByUserLoginId(userRequestDto.getUserLoginId());
            } else if (userRequestDto.getEmail() != null) {
                return userRepository.findByEmail(userRequestDto.getEmail());
            } else if (userRequestDto.getNickname() != null) {
                return userRepository.findByNickname(userRequestDto.getNickname());
            }
            throw new UserHandler(ErrorStatus.USER_BAD_REQUEST);

        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updatePassword(UserRequestDto userRequestDto) {
        User user = userRepository.findByUserLoginIdAndEmail(userRequestDto.getUserLoginId(), userRequestDto.getEmail())
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        user.setUserPw(passwordEncoder.encode(userRequestDto.getUserPw()));
    }




}
