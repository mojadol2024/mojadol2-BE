package com.gnu.pbl2.mail.controller;

import com.gnu.pbl2.exception.handler.MailHandler;
import com.gnu.pbl2.mail.dto.MailRequestDto;
import com.gnu.pbl2.mail.service.MailService;
import com.gnu.pbl2.response.ApiResponse;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.dto.UserRequestDto;
import com.gnu.pbl2.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mojadol/api/v1/mail")
public class MailController {

    private final MailService mailService;
    private final UserService userService;


    @PostMapping("/findUserId")
    public ResponseEntity<?> findUserId(@RequestBody UserRequestDto userRequestDto) {
        try {
            mailService.findUserId(userRequestDto);

            return ResponseEntity.ok(ApiResponse.onSuccess("메일 발송 성공"));

        } catch (Exception e) {
            throw new MailHandler(ErrorStatus.MAIL_SEND_FAILED);
        }
    }

    @PostMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody UserRequestDto userRequestDto) {
        try {
            mailService.findUserPassword(userRequestDto);

            return ResponseEntity.ok(ApiResponse.onSuccess("메일 발송 성공"));
        } catch (Exception e) {
            throw new MailHandler(ErrorStatus.MAIL_SEND_FAILED);
        }
    }

    @PostMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestBody MailRequestDto mailRequestDto){
        try {
            String response = mailService.mailCheck(mailRequestDto);

            return ResponseEntity.ok(ApiResponse.onSuccess(response));
        } catch (Exception e) {
            throw new MailHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UserRequestDto userRequestDto) {
        try {
            userService.updatePassword(userRequestDto);

            return ResponseEntity.ok(ApiResponse.onSuccess("비밀번호 변경 성공"));
        } catch (Exception e) {
            throw new MailHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
