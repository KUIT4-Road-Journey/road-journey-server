package com.road_journey.road_journey.my.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAccountPwRequestDto {

    @Schema(description = "새로운 비밀번호", example = "userpw4321!")
    private String accountPassword;

}
