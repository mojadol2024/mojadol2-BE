package com.gnu.pbl2.mail.service;

import com.gnu.pbl2.exception.handler.MailHandler;
import com.gnu.pbl2.mail.dto.MailDto;
import com.gnu.pbl2.mail.dto.MailRequestDto;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import com.gnu.pbl2.user.service.TokenService;
import com.gnu.pbl2.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final MailUtil mailUtil;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public void findUserId(UserRequestDto userRequestDto) {
        try {
            log.info("아이디 찾기 요청: email={}", userRequestDto.getEmail());

            User user = userRepository.findByEmail(userRequestDto.getEmail())
                    .orElseThrow(() -> new MailHandler(ErrorStatus.USER_NOT_FOUND));

            String message = mailUtil.mailHtml(user);

            MailDto mailDto = new MailDto();
            mailDto.setTitle("면접의 정석 " + user.getUsername() + "님 아이디 찾기");
            mailDto.setAddress(user.getEmail());
            mailDto.setMessage(message);

            mailUtil.mailSend(mailDto);

            log.info("아이디 찾기 메일 발송 완료: userId={}", user.getUserId());

        } catch (MailHandler e) {
            log.warn("아이디 찾기 실패: code={}, status={}, message={}",
                    e.getCode().getReason().getCode(),
                    e.getCode().getReasonHttpStatus().getCode(),
                    e.getCode().getReason().getMessage());

            throw new MailHandler(ErrorStatus.MAIL_PROCESS_FAILED);
        } catch (Exception e) {
            log.error("아이디 찾기 중 내부 에러", e);
            throw new MailHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void findUserPassword(UserRequestDto userRequestDto) {
        try {
            log.info("비밀번호 재설정 메일 요청: loginId={}, email={}", userRequestDto.getUserLoginId(), userRequestDto.getEmail());

            Random random = new Random();
            int min = 100000;
            int max = 999999;
            String verificationCode = String.valueOf(random.nextInt(max - min + 1) + min);

            String message = mailUtil.passwordMail(verificationCode);

            User user = userRepository.findByUserLoginIdAndEmail(userRequestDto.getUserLoginId(), userRequestDto.getEmail())
                    .orElseThrow(() -> new MailHandler(ErrorStatus.USER_NOT_FOUND));

            tokenService.saveMailToken(user.getUserLoginId() + user.getEmail(), verificationCode, 5, TimeUnit.MINUTES);

            MailDto mailDto = new MailDto();
            mailDto.setTitle("면접의 정석 " + user.getUsername() + "님 비밀번호 재설정");
            mailDto.setAddress(user.getEmail());
            mailDto.setMessage(message);

            mailUtil.mailSend(mailDto);

            log.info("비밀번호 재설정 인증코드 메일 발송 완료: userId={}", user.getUserId());
        } catch (Exception e) {
            log.error("비밀번호 재설정 메일 발송 중 에러", e);
            throw new MailHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String mailCheck(MailRequestDto mailRequestDto) {
        try {
            log.info("메일 인증 요청: loginId={}, email={}", mailRequestDto.getUserLoginId(), mailRequestDto.getEmail());

            String redisCode = tokenService.getMailToken(mailRequestDto.getUserLoginId() + mailRequestDto.getEmail());

            if (mailRequestDto.getCode().equals(redisCode)) {
                log.info("메일 인증 코드 일치");
                return "일치합니다.";
            } else {
                log.warn("메일 인증 코드 불일치");
                return "일치하지 않습니다.";
            }

        } catch (Exception e) {
            log.error("메일 인증 처리 중 에러", e);
            throw new MailHandler(ErrorStatus.MAIL_PROCESS_FAILED);
        }
    }
}
