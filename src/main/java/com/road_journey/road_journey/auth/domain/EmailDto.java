package com.road_journey.road_journey.auth.domain;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class EmailDto {

    // 이메일 주소
    private String email;

    // 인증 코드
    private String verifyCode;
}