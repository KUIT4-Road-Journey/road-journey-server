package com.road_journey.road_journey.items.dto;

import lombok.Getter;

@Getter
public class UserItemInfoDto {
    private final Long userItemId;
    private final Long itemId;
    private final String itemName;
    private final String category;
    private final Long growthPoint;
    private final Long growthLevel;
    private final boolean isSelected;

    public UserItemInfoDto(Long userItemId, Long itemId, String itemName, String category, Long growthPoint, Long growthLevel, boolean isSelected) {
        this.userItemId = userItemId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.growthPoint = growthPoint;
        this.growthLevel = growthLevel;
        this.isSelected = isSelected;
    }
}
