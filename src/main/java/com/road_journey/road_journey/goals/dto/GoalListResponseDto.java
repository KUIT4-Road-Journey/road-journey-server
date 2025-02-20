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
        private final boolean isSharedGoal;
        private final String sharedStatus;
        private final String progressStatus;
        private final int progress;
        private final LocalDate expireAt;
        private final String color;

        GoalInfo(Goal goal) {
            this.goalId = goal.getGoalId();
            this.title = goal.getTitle();
            this.difficulty = goal.getDifficulty();
            this.isSharedGoal = goal.isSharedGoal();
            this.sharedStatus = goal.getSharedStatus();
            this.progressStatus = goal.getProgressStatus();
            this.progress = goal.getProgress();
            this.expireAt = goal.getPeriodGoal().getExpireAt();
            this.color = checkColor();
        }

        private String checkColor() {
            if (progressStatus.equals("failed")) { // 실패한 목표인 경우
                return "red";
            }
            if (!sharedStatus.equals("registered") || progressStatus.equals("complete-pending")) {
                // 시작 대기 중이거나, 완료 대기 중인 경우
                return "white";
            }
            return "blue";
        }
    }
}
