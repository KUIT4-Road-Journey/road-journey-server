package com.road_journey.road_journey.items.dto;

import com.road_journey.road_journey.items.entity.Item;
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
    private Long growthPoint;
    private Long growthLevel;
    private boolean isSelected;

    public UserItemDto(UserItem userItem, Item item) {
        this.itemId = item.getItemId();
        this.itemName = item.getItemName();
        this.description = item.getDescription();
        this.gold = item.getGold();
        this.category = item.getCategory();
        this.growthPoint = userItem.getGrowthPoint() != null ? userItem.getGrowthPoint() : 0L;
        this.growthLevel = userItem.getGrowthLevel() != null ? userItem.getGrowthLevel() : 0L;
        this.isSelected = userItem.isSelected();
    }
}
