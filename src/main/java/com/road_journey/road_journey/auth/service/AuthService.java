package com.road_journey.road_journey.auth.service;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.dao.MemberRepository;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.LoginRequestDto;
import com.road_journey.road_journey.auth.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;
    @Transactional
    public String login(@Valid LoginRequestDto dto) {
        String accountId = dto.getAccountId();
        String password = dto.getPassword();
        Member member = memberRepository.findMemberByAccountId(accountId);
        if(member == null) {
            throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
        }

        // 암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
        if(!encoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        CustomUserInfoDto info = modelMapper.map(member, CustomUserInfoDto.class);

        String accessToken = jwtUtil.createAccessToken(info);
        return accessToken;
    }
}
