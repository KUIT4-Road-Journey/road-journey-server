package com.road_journey.road_journey.auth.service;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.LoginRequestDto;
import com.road_journey.road_journey.auth.domain.SignupRequestDto;
import com.road_journey.road_journey.auth.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;

    /**
     * 로그인 기능: accountId와 password로 로그인하고, 성공하면 JWT AccessToken 발급
     */
    @Transactional
    public String login(@Valid LoginRequestDto dto) {
        String accountId = dto.getAccountId();
        String password = dto.getPassword();
        User user = userRepository.findUserByAccountId(accountId);
        if(user == null) {
            throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
        }

        // 암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
        if(!encoder.matches(password, user.getAccountPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        CustomUserInfoDto info = modelMapper.map(user, CustomUserInfoDto.class);

        String accessToken = jwtUtil.createAccessToken(info);
        return accessToken;
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

        // 사용자 정보 저장
        User user = new User();
        user.setAccountId(request.getAccountId());
        user.setAccountPassword(encoder.encode(request.getAccountPassword())); // 비밀번호 암호화 저장
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setProfileImage(request.getProfileImage());
        user.setStatusMessage(request.getStatusMessage());

        userRepository.save(user);
        return "Registration successful";
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
}
