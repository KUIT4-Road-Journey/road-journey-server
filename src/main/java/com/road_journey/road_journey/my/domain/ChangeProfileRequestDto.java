package com.road_journey.road_journey.my.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProfileRequestDto {

    @Schema(description = "새로운 프로필 이미지 URL", example = "imageURL")
    private String profileImage;

    @Schema(description = "새로운 상태 메시지", example = "hello world!")
    private String statusMessage;

}
