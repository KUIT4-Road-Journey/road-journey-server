package com.road_journey.road_journey.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {
    private Long userId;       // 사용자 고유 ID
    private String accountId;  // 사용자 계정 ID
    private String email;      // 이메일
    private String nickname;   // 닉네임
    private String role;       // 사용자 역할
}
