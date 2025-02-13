package com.road_journey.road_journey.goals.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class AddGoalRequestDto {
    private String title;
    private int difficulty;
    private String category;
    private String description;
    private boolean sharedGoal;
    private String sharedGoalType;
    private boolean publicGoal;
    private String subGoalType;
    private DateInfo dateInfo;
    private List<Friend> friendList;
    private List<SubGoal> subGoalList;

    public boolean isRepeatedGoal() {
        return category.equals("repeated");
    }

    @Getter
    public static class DateInfo {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate startAt;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate expireAt;
        private int repetitionPeriod;
        private int repetitionNumber;

        // Getters and Setters
    }

    @Getter
    @AllArgsConstructor
    public static class Friend {
        private Long userId;

        // Getters and Setters
    }

    @Getter
    public static class SubGoal {
        private int index;
        private int difficulty;
        private String description;

        // Getters and Setters
    }
}
