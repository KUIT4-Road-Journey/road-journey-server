package com.road_journey.road_journey.auth.api;

import com.road_journey.road_journey.auth.domain.EmailDto;
import com.road_journey.road_journey.auth.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
//@RequestMapping("/auth")
public class EmailController {
    private final EmailService emailService;

    // 인증코드 메일 발송
//    @PostMapping("/send")
    public String mailSend(EmailDto emailDto) throws MessagingException {
        log.info("EmailController.mailSend()");
        emailService.sendEmail(emailDto.getEmail());
        return "인증코드가 발송되었습니다.";
    }

    // 인증코드 인증
//    @PostMapping("/verify")
    public String verify(EmailDto emailDto) {
        log.info("EmailController.verify()");
        boolean isVerify = emailService.verifyEmailCode(emailDto.getEmail(), emailDto.getVerifyCode());
        return isVerify ? "인증이 완료되었습니다." : "인증 실패하셨습니다.";
    }
}
