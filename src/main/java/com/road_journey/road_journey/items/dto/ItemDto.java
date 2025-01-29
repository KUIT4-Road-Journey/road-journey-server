package com.road_journey.road_journey.items.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long itemId;
    private String name;
    private String category;
    private boolean isSelected;
    private int growthPoint;
    private int growthLevel;
    private String description;
    private int gold;
}
