package com.road_journey.road_journey.items.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserItemDto {
    private Long itemId;
    private String itemName;
    private String description;
    private int gold;
    private String category;
    private int growthPoint;
    private int growthLevel;
    private boolean isSelected;
}
