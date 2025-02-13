package com.road_journey.road_journey.auth.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomUserInfoDto {

    private Long userId;

    private String accountId;

    private String accountPw;

    private String email;

    private String nickname;

    private String role;

}
