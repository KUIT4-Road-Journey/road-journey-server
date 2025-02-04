package com.road_journey.road_journey.items.dto;

import com.road_journey.road_journey.items.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialItemDto {
    private Long itemId;
    private String itemName;
    private String category;
    private String description;
    private boolean isOwned;

    public SpecialItemDto(Item item, boolean isOwned) {
        this.itemId = item.getItemId();
        this.itemName = item.getItemName();
        this.category = item.getCategory();
        this.description = item.getDescription();
        this.isOwned = isOwned;
    }
}
