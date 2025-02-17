package com.road_journey.road_journey.goals.dto;

import com.road_journey.road_journey.goals.domain.Goal;
import lombok.Getter;

@Getter
public class GoalRewardResponseDto {
    private final boolean isReward;
    private final Long gold;
    private final Long growthPoint;

    public GoalRewardResponseDto(Goal goal) {
        this.isReward = goal.isLastPeriodCompleted();
        this.gold = goal.getGold(isReward);
        this.growthPoint = goal.getGrowthPoint(isReward);
    }
}
