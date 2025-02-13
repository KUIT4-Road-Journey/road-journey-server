package com.road_journey.road_journey.auth.api;

import com.road_journey.road_journey.auth.domain.EmailDto;
import com.road_journey.road_journey.auth.domain.EmailRequestDto;
import com.road_journey.road_journey.auth.domain.VerifyCodeRequestDto;
import com.road_journey.road_journey.auth.service.EmailService;
import com.road_journey.road_journey.my.domain.AccountIdRequestDto;
import com.road_journey.road_journey.my.domain.ToggleRequestDto;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class EmailController {
    private final EmailService emailService;

    // 인증코드 메일 발송
    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody EmailRequestDto request) throws MessagingException {
        String code = emailService.sendEmail(request.getEmail());
        int requestCount = emailService.getRequestCount(request.getEmail());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("data", Map.of("code", code, "requestCount", requestCount));

        return ResponseEntity.ok(response);
    }

    // 아이디 찾기 - 인증번호 확인
    @PostMapping("/find/id/verify-code")
    public ResponseEntity<Map<String, Object>> verifyIdFindCode(@RequestBody VerifyCodeRequestDto request) {
        String accountId = emailService.findAccountIdByEmailAndCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("accountId", accountId)));

    }

    // 비밀번호 찾기 - 아이디 확인
    @PostMapping("/find/password/check-id")
    public ResponseEntity<Map<String, Object>> checkAccountIdForPwFind(@RequestBody AccountIdRequestDto request) {
        String email = emailService.findEmailByAccountId(request.getAccountId());

        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("email", email)));
    }

    // 비밀번호 찾기 - 인증번호 확인
    @PostMapping("/find/password/verify-code")
    public ResponseEntity<Map<String, String>> verifyPwFindCode(@RequestBody VerifyCodeRequestDto request) {
        boolean isValid = emailService.sendNewPassword(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("status", isValid ? "success" : "error", "message", isValid ? "New password sent to email" : "Invalid code"));
    }



    // 인증코드 인증
//    @PostMapping("/verify")
//    public String verify(EmailDto emailDto) {
//        log.info("EmailController.verify()");
//        boolean isVerify = emailService.verifyEmailCode(emailDto.getEmail(), emailDto.getVerifyCode());
//        return isVerify ? "인증이 완료되었습니다." : "인증 실패하셨습니다.";
//    }
}
