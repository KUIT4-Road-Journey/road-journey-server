package com.road_journey.road_journey.goals.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GoalListResponseDto {
    private final List<GoalInfo> goalInfoList;

    public GoalListResponseDto(List<GoalResponseDto> goalResponseList) {
        this.goalInfoList = new ArrayList<>();
        for(GoalResponseDto goalResponse : goalResponseList) {
            goalInfoList.add(new GoalInfo(goalResponse));
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

        GoalInfo(GoalResponseDto goalResponse) {
            this.goalId = goalResponse.getGoalId();
            this.title = goalResponse.getTitle();
            this.difficulty = goalResponse.getDifficulty();
            this.progressStutus = goalResponse.getProgressStatus();
            this.progress = goalResponse.getProgress();
            this.expireAt = goalResponse.getDateInfo().getExpireAt();
        }
    }
}
