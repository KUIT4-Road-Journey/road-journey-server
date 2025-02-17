package com.road_journey.road_journey.my.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChangeService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Transactional
    public void changeUserId(String currentUserId, String newId) {
        User user = userRepository.findById(Long.parseLong(currentUserId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (userRepository.findByAccountId(newId).isPresent()) {
            throw new RuntimeException("이미 존재하는 계정 ID입니다.");
        }
        user.setAccountId(newId);
    }

    @Transactional
    public void changeUserPassword(String currentUserId, String newPassword) {
        User user = userRepository.findById(Long.parseLong(currentUserId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!isValidPassword(newPassword)) {
            throw new RuntimeException("비밀번호는 영문, 숫자, 특수문자를 포함하여 10자 이상이어야 합니다.");
        }
        user.setAccountPw(encoder.encode(newPassword)); // 비밀번호 암호화 저장
    }

    @Transactional
    public void changeUserNickname(String currentUserId, String newNickname) {
        User user = userRepository.findById(Long.parseLong(currentUserId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setNickname(newNickname);
    }

    @Transactional
    public void changeUserEmail(String currentUserId, String newEmail) {
        User user = userRepository.findById(Long.parseLong(currentUserId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        user.setEmail(newEmail);
    }

    @Transactional
    public void changeUserProfile(String currentUserId, String profileImage, String statusMessage) {
        User user = userRepository.findById(Long.parseLong(currentUserId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setProfileImage(profileImage);
        user.setStatusMessage(statusMessage);
    }

    @Transactional
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$";
        return Pattern.matches(passwordPattern, password);
    }


}
