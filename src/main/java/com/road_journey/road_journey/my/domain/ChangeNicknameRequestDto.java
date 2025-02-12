package com.road_journey.road_journey.my.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeNicknameRequestDto {

    @Schema(description = "새로운 닉네임", example = "userNickname")
    private String nickname;

}
