package com.road_journey.road_journey.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.road_journey.road_journey.auth.config.RedisUtil;
import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Redis 직렬화/역직렬화
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final int MAX_REQUESTS = 5; // 이메일 전송 최대 횟수 (예: 5회)
    private static final long EXPIRE_TIME = 60 * 30L; // 30분 TTL
    private static final int PASSWORD_LENGTH = 10;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    // 인증코드 이메일 발송
    public String sendEmail(String toEmail) throws MessagingException {
        // 현재 전송 횟수 가져오기
        int requestCount = getRequestCount(toEmail);

        if (requestCount >= MAX_REQUESTS) {
            throw new RuntimeException("최대 이메일 전송 횟수를 초과했습니다.");
        }

        // 새로운 인증 코드 생성
        String code = generateVerificationCode();

        // Redis에 인증번호 + 요청 횟수 저장
        saveToRedis(toEmail, code, requestCount + 1);

        // 이메일 폼 생성 및 전송
        MimeMessage emailForm = createEmailForm(toEmail, code);
        javaMailSender.send(emailForm);

        return code;
    }

    // 아이디 찾기 - 인증번호 확인
    public String findAccountIdByEmailAndCode(String email, String code) {
        boolean isValid = verifyEmailCode(email, code);
        if (!isValid) {
            throw new RuntimeException("Invalid verification code.");
        }

        return userRepository.findByEmail(email)
                .map(User::getAccountId)
                .orElseThrow(() -> new RuntimeException("Account not found."));
    }

    // 비밀번호 찾기 - 아이디 확인
    public String findEmailByAccountId(String accountId) {
        return userRepository.findByAccountId(accountId)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("Account not found."));
    }

    // 비밀번호 찾기 - 인증번호 확인 및 새 비밀번호 저장
    public boolean sendNewPassword(String email, String code) {
        boolean isValid = verifyEmailCode(email, code);
        if (!isValid) {
            throw new RuntimeException("Invalid verification code.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found."));

        String newPassword = generateRandomPassword();
        user.setAccountPw(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        try {
            sendNewPasswordEmail(email, newPassword);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email.");
        }

        return true;
    }

    // 코드 검증
    private Boolean verifyEmailCode(String email, String code) {
        // Redis에서 데이터 가져오기
        Map<String, Object> data = getFromRedis(email);
        if (data == null || !data.containsKey("code")) {
            return false;
        }

        String storedCode = (String) data.get("code");
        return storedCode.equals(code);
    }

    private void saveToRedis(String email, String code, int requestCount) {
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        data.put("requestCount", requestCount);

        try {
            String jsonData = objectMapper.writeValueAsString(data);
            redisUtil.setDataExpire(email, jsonData, EXPIRE_TIME);
        } catch (JsonProcessingException e) {
            log.error("Redis 데이터 직렬화 실패: {}", e.getMessage());
        }
    }

    private Map<String, Object> getFromRedis(String email) {
        try {
            String jsonData = redisUtil.getData(email);
            if (jsonData != null) {
                return objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
            }
        } catch (JsonProcessingException e) {
            log.error("Redis 데이터 역직렬화 실패: {}", e.getMessage());
        }
        return null;
    }

    // 이메일 내용 초기화
    private String setContext(String code) {
        Context context = new Context();
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        context.setVariable("code", code);

        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process("mail", context);
    }

    // 6자리 난수 생성
    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    // 이메일 폼 생성
    private MimeMessage createEmailForm(String email, String code) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("Road Journey 인증번호입니다.");
        message.setFrom(senderEmail);
        message.setText(setContext(code), "utf-8", "html");

        return message;
    }

    // 이메일 요청 횟수 가져오기
    public int getRequestCount(String email) {
        String countStr = redisUtil.getData(email + ":requestCount");
        return countStr != null ? Integer.parseInt(countStr) : 0;
    }

    // 새 비밀번호 생성
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    // 이메일로 새 비밀번호 전송
    private void sendNewPasswordEmail(String email, String newPassword) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("Road Journey 임시 비밀번호 발급 안내");
        message.setFrom(senderEmail);
        message.setText("안녕하세요.\n\n귀하의 새로운 임시 비밀번호는 다음과 같습니다:\n\n" +
                newPassword + "\n\n로그인 후 비밀번호를 변경해주세요.", "utf-8");

        javaMailSender.send(message);
    }

}
