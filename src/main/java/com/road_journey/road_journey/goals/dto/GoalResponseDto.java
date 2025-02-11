package com.road_journey.road_journey.goals.dto;

import com.road_journey.road_journey.goals.domain.Goal;
import com.road_journey.road_journey.goals.domain.PeriodGoal;
import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private final List<RepetitionInfo> repetitionInfoList;

    public GoalResponseDto(Goal goal, List<Long> friendIdList) {
        this.goalId = goal.getGoalId();
        this.title = goal.getTitle();
        this.difficulty = goal.getDifficulty();
        this.category = goal.getCategory();
        this.description = goal.getDescription();
        this.progressStatus = goal.getProgressStatus();
        this.progress = goal.getProgress();
        this.isSharedGoal = goal.isSharedGoal();
        this.isPublicGoal = goal.isPublic();
        this.subGoalType = goal.getSubGoalType();
        this.dateInfo = new DateInfo(goal.getPeriodGoal(), goal.getRepeatedGoal());
        this.friendList = createFriendList(friendIdList);
        this.subGoalList = createSubGoalList(goal.getSubGoalList());
        this.repetitionInfoList = createRepetitionInfoList(goal.getPeriodGoal(), goal.getRepeatedGoal());
    }

    private List<RepetitionInfo> createRepetitionInfoList(PeriodGoal periodGoal, RepeatedGoal repeatedGoal) {
        if (repeatedGoal == null) {
            return null;
        }

        LocalDate startAt = periodGoal.getStartAt();
        int repetitionPeriod = repeatedGoal.getRepetitionPeriod();
        String repetitionHistory = repeatedGoal.getRepetitionHistory();

        List<RepetitionInfo> repetitionInfoList = new ArrayList<>();
        for (int i = 0; i < repetitionHistory.length(); i++) {
            LocalDate periodStartAt = startAt.plusDays((long) repetitionPeriod * i);
            LocalDate periodExpireAt = periodStartAt.plusDays((long) repetitionPeriod);
            repetitionInfoList.add(new RepetitionInfo(periodStartAt, periodExpireAt, repetitionHistory.charAt(i)));
        }
        return repetitionInfoList;
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

    @Getter
    public static class DateInfo {
        private final LocalDate startAt;
        private final LocalDate expireAt;
        private final LocalDate periodStartAt;
        private final LocalDate periodExpireAt;
        private final int repetitionPeriod;
        private final int repetitionNumber;

        DateInfo(PeriodGoal periodGoal, RepeatedGoal repeatedGoal) {
            this.startAt = periodGoal.getStartAt();
            this.expireAt = periodGoal.getExpireAt();
            this.periodStartAt = periodGoal.getPeriodStartAt();
            this.periodExpireAt = periodGoal.getPeriodExpireAt();

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

    @Getter
    public static class RepetitionInfo {
        private final LocalDate startAt;
        private final LocalDate expireAt;
        private final String progressStatus;

        RepetitionInfo(LocalDate startAt, LocalDate expireAt, char c) {
            this.startAt = startAt;
            this.expireAt = expireAt;
            this.progressStatus = c == '1' ? "completed" : "failed";
        }
    }
}
