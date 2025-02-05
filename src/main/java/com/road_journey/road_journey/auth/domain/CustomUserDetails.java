package com.road_journey.road_journey.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final CustomUserInfoDto member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();

        // 기본 역할 설정
        if (member.getRole() == null) {
            roles.add("ROLE_USER");  // 기본적으로 사용자에게 "ROLE_USER" 부여
        } else {
            roles.add("ROLE_" + member.getRole());
        }

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getMemberId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
