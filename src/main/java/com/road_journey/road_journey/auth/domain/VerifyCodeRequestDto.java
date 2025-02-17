package com.road_journey.road_journey.auth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeRequestDto {
    private String email;
    private String code;
}
