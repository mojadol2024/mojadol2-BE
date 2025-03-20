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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailUtil mailUtil;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public void findUserId(UserRequestDto userRequestDto) {
        try {
            User user = userRepository.findByEmail(userRequestDto.getEmail())
                    .orElseThrow(() -> new MailHandler(ErrorStatus.USER_NOT_FOUND));

            String message = mailUtil.mailHtml(user);

            MailDto mailDto = new MailDto();
            mailDto.setTitle("면접의 정석 " + user.getUsername() + "님 아이디 찾기");
            mailDto.setAddress(user.getEmail());
            mailDto.setMessage(message);

            mailUtil.mailSend(mailDto);
        } catch (MailHandler e) {
            throw new MailHandler(ErrorStatus.MAIL_PROCESS_FAILED);
        } catch (Exception e) {
            throw new MailHandler(ErrorStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public void findUserPassword(UserRequestDto userRequestDto) {
        try {

            Random random = new Random();
            int min = 100000;
            int max = 999999;
            String verificationCode = String.valueOf(random.nextInt(max - min + 1) + min);

            String message = mailUtil.passwordMail(verificationCode);

            User user = userRepository.findByUserLoginIdAndEmail(userRequestDto.getUserLoginId(), userRequestDto.getEmail())
                    .orElseThrow(() -> new MailHandler(ErrorStatus.USER_NOT_FOUND));

            tokenService.saveToken(user.getUserLoginId() + user.getEmail(), verificationCode, 5, TimeUnit.MINUTES);

            MailDto mailDto = new MailDto();
            mailDto.setTitle("면접의 정석 " + user.getUsername() + "님 아이디 찾기");
            mailDto.setAddress(user.getEmail());
            mailDto.setMessage(message);

            mailUtil.mailSend(mailDto);
        } catch (Exception e) {
            throw new MailHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String mailCheck(MailRequestDto mailRequestDto) {
        try {
            String redisCode = tokenService.getToken(mailRequestDto.getUserLoginId() + mailRequestDto.getEmail());

            if (mailRequestDto.getCode().equals(redisCode)) {
                return "일치합니다.";
            }else{
                return "일치하지 않습니다.";
            }

        } catch (Exception e) {
            throw new MailHandler(ErrorStatus.MAIL_PROCESS_FAILED);
        }
    }

}
