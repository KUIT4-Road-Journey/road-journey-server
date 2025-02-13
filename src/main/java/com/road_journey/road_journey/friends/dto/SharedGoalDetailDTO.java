package com.road_journey.road_journey.friends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SharedGoalDetailDTO {
    private String goalTitle;
    private String description;
    private String startDate;
    private String endDate;
    private double difficulty;
    private int remainingDays;
    private List<StepDTO> steps;
}