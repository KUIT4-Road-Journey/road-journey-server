package com.road_journey.road_journey.friends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StepDTO {
    private int stepNumber;
    private String description;
    private boolean isCompleted;
}
