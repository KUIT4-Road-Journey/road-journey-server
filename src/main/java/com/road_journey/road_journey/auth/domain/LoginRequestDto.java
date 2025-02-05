package com.road_journey.road_journey.auth.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "AUTH_REQ_01 : 로그인 요청 DTO")
public class LoginRequestDto {

    @NotNull(message = "아이디 입력은 필수입니다.")
    private String accountId;


    @NotNull(message = "패스워드 입력은 필수입니다.")
    private String password;
}
