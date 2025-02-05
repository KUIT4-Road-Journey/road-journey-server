package com.road_journey.road_journey.auth.service;

import com.road_journey.road_journey.auth.dao.MemberRepository;
import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.Member;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ModelMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        CustomUserInfoDto dto = mapper.map(member, CustomUserInfoDto.class);

        return new CustomUserDetails(dto);
    }
}
