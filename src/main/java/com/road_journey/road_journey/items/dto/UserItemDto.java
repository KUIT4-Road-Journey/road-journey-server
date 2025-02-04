package com.road_journey.road_journey.items.dto;

import com.road_journey.road_journey.items.entity.UserItem;
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

    public UserItemDto(UserItem userItem) {
        this.itemId = userItem.getItem().getItemId();
        this.itemName = userItem.getItem().getItemName();
        this.description = userItem.getItem().getDescription();
        this.gold = userItem.getItem().getGold();
        this.category = userItem.getItem().getCategory();
        this.growthPoint = userItem.getGrowthPoint();
        this.growthLevel = userItem.getGrowthLevel();
        this.isSelected = userItem.isSelected();
    }
}
