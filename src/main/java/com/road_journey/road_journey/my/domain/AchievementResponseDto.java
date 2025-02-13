package com.road_journey.road_journey.my.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AchievementResponseDto {
    private String status;
    private List<AchievementDto> data;
}
