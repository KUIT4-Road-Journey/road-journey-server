package com.road_journey.road_journey.archives.dto;

import com.road_journey.road_journey.goals.domain.Goal;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ArchiveListResponseDto {
    private final List<ArchiveInfo> archiveInfoList;

    public ArchiveListResponseDto(List<Goal> goalList) {
        this.archiveInfoList = new ArrayList<>();
        for(Goal goal : goalList) {
            archiveInfoList.add(new ArchiveListResponseDto.ArchiveInfo(goal));
        }
    }

    @Getter
    public static class ArchiveInfo {
        private final Long goalId;
        private final String title;
        private final int difficulty;
        private final int progress;
        private final LocalDate createdAt;
        private final LocalDate finishedAt;
        private final int completeCount;
        private final int failedCount;

        ArchiveInfo(Goal goal) {
            this.goalId = goal.getGoalId();
            this.title = goal.getTitle();
            this.difficulty = goal.getDifficulty();
            this.progress = goal.getProgress();
            this.createdAt = LocalDate.from(goal.getCreatedAt());
            this.finishedAt = goal.getFinishedAt() == null ? null : LocalDate.from(goal.getFinishedAt());
            this.completeCount = goal.isRepeatedGoal() ? goal.getRepeatedGoal().getCompletedCount() : -1;
            this.failedCount = goal.isRepeatedGoal() ? goal.getRepeatedGoal().getFailedCount() : -1;
        }
    }
}
