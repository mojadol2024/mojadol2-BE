package com.gnu.pbl2.utils;

import com.gnu.pbl2.exception.handler.MailHandler;
import com.gnu.pbl2.mail.dto.MailDto;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class MailUtil {

    private final JavaMailSender javaMailSender;

    public String mailHtml(User user) {
        try {
            String userId = user.getUserLoginId();
            if (userId.length() > 2) {
                userId = userId.substring(0, userId.length() - 2) + "**";
            }

            String message = "";

            message += "<!DOCTYPE html>";
            message += "<html lang='en'>";
            message += "<head>";
            message += "    <meta charset='UTF-8'>";
            message += "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>";
            message += "    <style>";
            message += "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9; }";
            message += "        .email-container { max-width: 600px; margin: 30px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }";
            message += "        .header { text-align: center; font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px; }";
            message += "        .content { font-size: 16px; color: #555; line-height: 1.5; }";
            message += "        .content p { margin: 10px 0; }";
            message += "        .content .highlight { font-weight: bold; color: #007BFF; }";
            message += "        .footer { text-align: center; margin-top: 20px; font-size: 14px; color: #aaa; }";
            message += "    </style>";
            message += "</head>";
            message += "<body>";
            message += "    <div class='email-container'>";
            message += "        <div class='header'> 면접의 정석 아이디 찾기 결과 </div>";
            message += "        <div class='content'>";
            message += "            <p>안녕하세요, <span class='highlight'>" + user.getUsername() + "</span>회원님!</p>";
            message += "            <p>요청하신 아이디는 다음과 같습니다:</p>";
            message += "            <p class='highlight'>" + userId + "</p>";
            message += "            <p>아이디를 안전하게 보관하시고, 로그인 정보를 타인과 공유하지 마세요.</p>";
            message += "        </div>";
            message += "        <div class='footer'>이 메일은 발신 전용입니다. 문의 사항은 고객센터를 이용해 주세요.</div>";
            message += "    </div>";
            message += "</body>";
            message += "</html>";

            return message;
        }
        catch (Exception e) {
            throw new MailHandler(ErrorStatus.MAIL_TEMPLATE_NOT_FOUND);
        }

    }

    public String passwordMail(String verificationCode) {

        String message = "";

        message += "<!DOCTYPE html>";
        message += "<html lang='en'>";
        message += "<head>";
        message += "    <meta charset='UTF-8'>";
        message += "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>";
        message += "    <style>";
        message += "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f9f9f9; }";
        message += "        .email-container { max-width: 600px; margin: 30px auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }";
        message += "        .header { text-align: center; font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px; }";
        message += "        .content { font-size: 16px; color: #555; line-height: 1.5; }";
        message += "        .content p { margin: 10px 0; }";
        message += "        .content .highlight { font-weight: bold; color: #007BFF; }";
        message += "        .footer { text-align: center; margin-top: 20px; font-size: 14px; color: #aaa; }";
        message += "    </style>";
        message += "</head>";
        message += "<body>";
        message += "    <div class='email-container'>";
        message += "        <div class='header'> 면접의 정석 메일 본인 인증 코드 </div>";
        message += "        <div class='content'>";
        message += "            <p>안녕하세요, 회원님!</p>";
        message += "            <p>면접의 정석 본인 인증 번호입니다.</p>";
        message += "            <h4 class='highlight'>" + verificationCode + "</h4>";
        message += "            <p>코드를 안전하게 보관하시고, 로그인 정보를 타인과 공유하지 마세요.</p>";
        message += "        </div>";
        message += "        <div class='footer'>이 메일은 발신 전용입니다. 문의 사항은 고객센터를 이용해 주세요.</div>";
        message += "    </div>";
        message += "</body>";
        message += "</html>";

        return message;
    }


    public void mailSend(MailDto mailDto) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(mailDto.getAddress());
            helper.setSubject(mailDto.getTitle());
            helper.setText(mailDto.getMessage(), true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailHandler(ErrorStatus.MAIL_SEND_FAILED);
        }
    }


}
