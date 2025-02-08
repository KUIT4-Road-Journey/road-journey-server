package com.road_journey.road_journey.goals.dto;

import com.road_journey.road_journey.goals.domain.Goal;
import com.road_journey.road_journey.goals.domain.PeriodGoal;
import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class GoalResponseDto {
    private final Long goalId;
    private final String title;
    private final int difficulty;
    private final String category;
    private final String description;
    private final String progressStatus;
    private final int progress;
    private final boolean isSharedGoal;
    private final boolean isPublicGoal;
    private final String subGoalType;
    private final DateInfo dateInfo;
    private final List<Friend> friendList;
    private final List<SubGoal> subGoalList;

    public GoalResponseDto(Goal goal, List<Long> friendIdList) {
        this.goalId = goal.getGoalId();
        this.title = goal.getTitle();
        this.difficulty = goal.getDifficulty();
        this.category = goal.getCategory();
        this.description = goal.getDescription();
        this.progressStatus = goal.getProgressStatus();
        this.progress = calculateProgress(goal.getSubGoalList());
        this.isSharedGoal = goal.isSharedGoal();
        this.isPublicGoal = goal.isPublic();
        this.subGoalType = goal.getSubGoalType();
        this.dateInfo = new DateInfo(goal.getPeriodGoal(), goal.getRepeatedGoal());
        this.friendList = createFriendList(friendIdList);
        this.subGoalList = createSubGoalList(goal.getSubGoalList());
    }

    public static GoalResponseDto from(Goal goal, List<Long> friendIdList) {
        return new GoalResponseDto(goal, friendIdList);
    }

    private List<SubGoal> createSubGoalList(List<com.road_journey.road_journey.goals.domain.SubGoal> subGoalList) {
        List<SubGoal> subGoalResponseList = new ArrayList<>();
        for(com.road_journey.road_journey.goals.domain.SubGoal subGoal : subGoalList) {
            subGoalResponseList.add(new SubGoal(subGoal));
        }
        return subGoalResponseList;
    }

    private List<Friend> createFriendList(List<Long> friendIdList) {
        List<Friend> friendList = new ArrayList<>();
        for(Long friendId : friendIdList) {
            friendList.add(new Friend(friendId));
        }
        return friendList;
    }

    private int calculateProgress(List<com.road_journey.road_journey.goals.domain.SubGoal> subGoalList) {
        if (subGoalList.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (com.road_journey.road_journey.goals.domain.SubGoal subGoal : subGoalList) {
            if (subGoal.getProgressStatus().equals("completed")) {
                count ++;
            }
        }
        return (int)(count / (double)(subGoalList.size() + 1) * 100);
    }

    @Getter
    public static class DateInfo {
        private final LocalDate startAt;
        private final LocalDate expireAt;
        private final int repetitionPeriod;
        private final int repetitionNumber;

        DateInfo(PeriodGoal periodGoal, RepeatedGoal repeatedGoal) {
            this.startAt = periodGoal.getPeriodStartAt();
            this.expireAt = periodGoal.getPeriodExpireAt();

            if (repeatedGoal != null) {
                this.repetitionPeriod = repeatedGoal.getRepetitionPeriod();
                this.repetitionNumber = repeatedGoal.getRepetitionNumber();
                return;
            }
            this.repetitionPeriod = -1;
            this.repetitionNumber = -1;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Friend {
        private Long userId;
    }

    @Getter
    public static class SubGoal {
        private final Long subGoalId;
        private final int index;
        private final int difficulty;
        private final String description;
        private final String progressStatus;

        SubGoal(com.road_journey.road_journey.goals.domain.SubGoal subGoal) {
            this.subGoalId = subGoal.getSubGoalId();
            this.index = subGoal.getSubGoalIndex();
            this.difficulty = subGoal.getDifficulty();
            this.description = subGoal.getDescription();
            this.progressStatus = subGoal.getProgressStatus();
        }
    }
}
