package com.road_journey.road_journey.goals.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AddGoalRequestDto {
    private String title;
    private int difficulty;
    private String category;
    private String description;
    private boolean isSharedGoal;
    private boolean isPublicGoal;
    private String subGoalType;
    private DateInfo dateInfo;
    private List<Friend> friendList;
    private List<SubGoal> subGoalList;

    // Getters and Setters

    @Getter
    public static class DateInfo {
        private String startDate;
        private String expirationDate;
        private String repetitionPeriod;
        private Integer repetitionNumber;

        // Getters and Setters
    }

    @Getter
    public static class Friend {
        private String userId;

        // Getters and Setters
    }

    @Getter
    public static class SubGoal {
        private int index;
        private double difficulty;
        private String description;

        // Getters and Setters
    }
}
