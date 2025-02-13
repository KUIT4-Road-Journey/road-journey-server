package com.road_journey.road_journey.my.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AchievementDto {
    private Long id;
    private String achievementName;
    private String description;
    private int gold;
    private int growthPoint;
    private int progress;
    @JsonProperty("isRewardAccepted")
    private boolean rewardAccepted;

    public static AchievementDto from(UserAchievement userAchievement) {
        return new AchievementDto(
                userAchievement.getAchievement().getId(),
                userAchievement.getAchievement().getAchievementName(),
                userAchievement.getAchievement().getDescription(),
                userAchievement.getAchievement().getGold(),
                userAchievement.getAchievement().getGrowthPoint(),
                userAchievement.getProgress(),
                userAchievement.isRewardAccepted()
        );
    }
}
