package com.gnu.pbl2.user.service;

import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.UserProfileDto;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.dto.UserResponseDto;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.entity.enums.Tier;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.utils.JwtUtil;
import com.gnu.pbl2.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final VoucherService voucherService;

    public String signUp(UserRequestDto userRequestDto) {
        try {
            User user = userRequestDto.toEntity(userRequestDto);
            user.setTier(Tier.FREE);
            user.setUserPw(passwordEncoder.encode(user.getUserPw()));

            User response = userRepository.save(user);

            voucherService.freeVoucher(response);
            log.info("회원가입 성공: userId={}, username={}", response.getUserId(), response.getUsername());
            return response.getUsername();

        } catch (DataIntegrityViolationException e) {
            log.warn("회원가입 중 중복 에러 발생: {}", e.getMessage());
            throw new UserHandler(ErrorStatus.USER_SQL_UNIQUE);

        } catch (Exception e) {
            log.error("회원가입 중 내부 에러", e);
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
            log.error("회원가입 중복 검사 중 내부 에러", e);
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updatePassword(UserRequestDto userRequestDto) {
        User user = userRepository.findByUserLoginIdAndEmail(userRequestDto.getUserLoginId(), userRequestDto.getEmail())
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        user.setUserPw(passwordEncoder.encode(userRequestDto.getUserPw()));
        log.info("비밀번호 재설정 완료: userId={}", user.getUserId());
    }

    public boolean checkPassword(UserRequestDto userRequestDto, String accessToken) {
        Long userId = jwtUtil.extractUserId(accessToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        boolean match = passwordEncoder.matches(userRequestDto.getUserPw(), user.getUserPw());
        log.info("비밀번호 확인 결과: userId={}, 일치여부={}", userId, match);
        return match;
    }

    public String updateProfile(UserRequestDto userRequestDto, String accessToken) {
        Long userId = jwtUtil.extractUserId(accessToken);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

            user.setUserPw(passwordEncoder.encode(userRequestDto.getUserPw()));
            user.setNickname(userRequestDto.getNickname());

            userRepository.save(user);
            log.info("프로필 수정 완료: userId={}", userId);

            return user.getUsername();
        } catch (Exception e) {
            log.error("프로필 수정 중 에러 발생: userId={}", userId, e);
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String resignUser(String accessToken) {
        Long userId = jwtUtil.extractUserId(accessToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        try {
            user.setDeletedTime(LocalDateTime.now().plusMonths(3));
            userRepository.save(user);
            log.info("회원 탈퇴 예약 완료: userId={}, deletedTime={}", userId, user.getDeletedTime());
        } catch (Exception e) {
            log.error("회원 탈퇴 처리 중 예외 발생: userId={}", userId, e);
            throw new UserHandler(ErrorStatus.USER_RESIGN_FAILED);
        }

        return "회원 탈퇴 예약이 완료되었습니다.";
    }

    public UserProfileDto profile(String accessToken) {
        Long userId = jwtUtil.extractUserId(accessToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        try {
            UserProfileDto response = new UserProfileDto();
            response.setEmail(user.getEmail());
            response.setUserName(user.getUsername());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setNickname(user.getNickname());
            response.setUserLoginId(user.getUserLoginId());

            return response;
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 스케줄러로 userdeletedtime이 되면 삭제 매일 낮12시에 작동
    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    public void deleteResignedUsers() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<User> resignedUsers = userRepository.findByDeletedTimeBefore(now);

            for (User user : resignedUsers) {
                log.info("회원 삭제 처리: userId={}", user.getUserId());
                userRepository.delete(user);
            }
        } catch (Exception e) {
            log.error("회원 삭제 스케줄러 예외 발생", e);
            throw new UserHandler(ErrorStatus.USER_DELETE_SCHEDULE_FAILED);
        }
    }
}
