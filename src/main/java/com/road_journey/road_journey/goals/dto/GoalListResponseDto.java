package com.road_journey.road_journey.goals.dto;

import com.road_journey.road_journey.goals.domain.Goal;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GoalListResponseDto {
    private final List<GoalInfo> goalInfoList;

    public GoalListResponseDto(List<Goal> goalList) {
        this.goalInfoList = new ArrayList<>();
        for(Goal goal : goalList) {
            goalInfoList.add(new GoalInfo(goal));
        }
    }

    @Getter
    public static class GoalInfo {
        private final Long goalId;
        private final String title;
        private final int difficulty;
        private final String progressStutus;
        private final int progress;
        private final LocalDate expireAt;

        GoalInfo(Goal goal) {
            this.goalId = goal.getGoalId();
            this.title = goal.getTitle();
            this.difficulty = goal.getDifficulty();
            this.progressStutus = goal.getProgressStatus();
            this.progress = goal.getProgress();
            this.expireAt = goal.getPeriodGoal().getExpireAt();
        }
    }
}
