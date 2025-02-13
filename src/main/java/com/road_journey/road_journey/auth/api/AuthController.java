package com.road_journey.road_journey.auth.api;

import com.road_journey.road_journey.auth.domain.LoginRequestDto;
import com.road_journey.road_journey.auth.domain.SignupRequestDto;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> getUserProfile(
            @Valid @RequestBody LoginRequestDto request
    ) {
        String token = this.authService.login(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("data", Map.of("accessToken", token));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody SignupRequestDto request) {
        String message = authService.signup(request);
        return ResponseEntity.ok(Map.of("status", "success", "message", message));
    }

    // TODO: 이메일 인증 로직 필요
    // 이메일 인증 요청 API
//    @PostMapping("/email")
//    public ResponseEntity<Map<String, Object>> sendEmailVerification(@RequestBody Map<String, String> request) {
//        String email = request.get("email");
//        int code = emailService.sendVerificationCode(email);
//        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("code", code, "requestCount", 3)));
//    }

    // 아이디 찾기 - 인증 코드 확인
    //@PostMapping("/find/id/verify-code")
    public ResponseEntity<Map<String, Object>> verifyIdCode(@RequestBody Map<String, Integer> request) {
        int code = request.get("code");
        Optional<User> user = authService.findIdByEmail("user@email.com"); // TODO: 이메일 기반 조회 로직 필요
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("accountId", user.get().getAccountId())));
        }
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid code"));
    }

    // 비밀번호 찾기 - 인증 코드 확인
    //@PostMapping("/find/password/verify-code")
    public ResponseEntity<Map<String, Object>> verifyPasswordCode(@RequestBody Map<String, Integer> request) {
        int code = request.get("code");
        Optional<User> user = authService.findPasswordByEmail("user@email.com"); // TODO: 이메일 기반 조회 로직 필요
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("accountId", user.get().getAccountId())));
        }
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid code"));
    }

    // 비밀번호 찾기 - 아이디 확인
    //@PostMapping("/find/password/check-id")
    public ResponseEntity<Map<String, Object>> checkId(@RequestBody Map<String, String> request) {
        String accountId = request.get("accountId");
        Optional<User> user = authService.findByAccountId(accountId);
        if (user.isPresent()) {
            // TODO: 이메일로 임시 비밀번호 전송 로직 필요
            return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("email", user.get().getEmail())));
        }
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Account not found"));
    }
}
