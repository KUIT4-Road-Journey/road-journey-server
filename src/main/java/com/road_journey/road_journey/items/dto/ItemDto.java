package com.road_journey.road_journey.items.dto;

import com.road_journey.road_journey.items.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long itemId;
    private String itemName;
    private String category;
    private String description;
    private int gold;
    private boolean isSelected;
    private boolean isOwned;

    public ItemDto(Item item) {
        this.itemId = item.getItemId();
        this.itemName = item.getItemName();
        this.category = item.getCategory();
        this.description = item.getDescription();
        this.gold = item.getGold();
        this.isSelected = false;
        this.isOwned = false;
    }
}
