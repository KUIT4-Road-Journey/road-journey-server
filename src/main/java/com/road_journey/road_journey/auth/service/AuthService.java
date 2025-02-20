package com.road_journey.road_journey.auth.service;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.LoginRequestDto;
import com.road_journey.road_journey.auth.domain.LoginResponseDto;
import com.road_journey.road_journey.auth.domain.SignupRequestDto;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import com.road_journey.road_journey.my.dao.AchievementRepository;
import com.road_journey.road_journey.my.dao.SettingRepository;
import com.road_journey.road_journey.my.dao.UserAchievementRepository;
import com.road_journey.road_journey.my.dao.UserSettingRepository;
import com.road_journey.road_journey.my.domain.Achievement;
import com.road_journey.road_journey.my.domain.UserAchievement;
import com.road_journey.road_journey.my.domain.UserSetting;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;
    private final UserSettingRepository userSettingRepository;
    private final SettingRepository settingRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final UserItemRepository userItemRepository;

    /**
     * 로그인 기능: accountId와 password로 로그인하고, 성공하면 JWT AccessToken 발급
     */

    @Transactional
    public LoginResponseDto login(@Valid LoginRequestDto dto) {
        String accountId = dto.getAccountId();
        String password = dto.getAccountPw();
        User user = userRepository.findUserByAccountId(accountId);

        if (user == null) {
            throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
        }

        // 암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
        if (!encoder.matches(password, user.getAccountPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 lastLoginTime 업데이트
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
      
        CustomUserInfoDto info = modelMapper.map(user, CustomUserInfoDto.class);
        String accessToken = jwtUtil.createAccessToken(info);

        return new LoginResponseDto(accessToken, user.getUserId());
    }

    /**
     * 회원가입 기능: 새로운 사용자를 저장
     */
    @Transactional
    public String signup(@Valid SignupRequestDto request) {
        // 중복된 계정 ID 또는 이메일 체크
        if (userRepository.findByAccountId(request.getAccountId()).isPresent()) {
            throw new RuntimeException("이미 존재하는 계정 ID입니다.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 검증 (영문 + 숫자 + 특수문자 포함, 10자 이상)
        if (!isValidPassword(request.getAccountPw())) {
            throw new RuntimeException("비밀번호는 영문, 숫자, 특수문자를 포함하여 10자 이상이어야 합니다.");
        }


        // 사용자 정보 저장
        User user = new User();
        user.setAccountId(request.getAccountId());
        user.setAccountPw(encoder.encode(request.getAccountPw())); // 비밀번호 암호화 저장
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setProfileImage(request.getProfileImage());
        user.setStatusMessage(request.getStatusMessage());

        userRepository.save(user);

        addDefaultItemsToUser(user.getUserId());
        createDefaultSettings(user);
        createDefaultAchievements(user);

        return "Registration successful";
    }

    // 비밀번호 검증 메서드
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$";
        return Pattern.matches(passwordPattern, password);
    }

    private void createDefaultSettings(User user) {
        List<UserSetting> defaultSettings = List.of(
                new UserSetting(user, settingRepository.findById(1L).get(), "DISABLED"),
                new UserSetting(user, settingRepository.findById(2L).get(), "DISABLED"),
                new UserSetting(user, settingRepository.findById(3L).get(), "DISABLED")
        );

        userSettingRepository.saveAll(defaultSettings);
    }

    private void createDefaultAchievements(User user) {
        // Achievement 테이블에 존재하는 모든 업적 가져오기
        List<Achievement> allAchievements = achievementRepository.findAll();

        // UserAchievement 객체로 변환
        List<UserAchievement> defaultAchievements = allAchievements.stream()
                .map(achievement -> new UserAchievement(user, achievement))
                .collect(Collectors.toList());

        // 저장
        userAchievementRepository.saveAll(defaultAchievements);
    }

    /**
     * 이메일로 사용자 찾기
     */
    public Optional<User> findIdByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findPasswordByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 아이디로 사용자 찾기
     */
    public Optional<User> findByAccountId(String accountId) {
        return userRepository.findByAccountId(accountId);
    }

    private void addDefaultItemsToUser(Long userId) {
        List<UserItem> defaultItems = List.of(
                new UserItem(null, userId, 1L, true, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()),
                new UserItem(null, userId, 15L, true, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now())
        );

        userItemRepository.saveAll(defaultItems);
    }
}
