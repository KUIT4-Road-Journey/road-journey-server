package com.road_journey.road_journey.items.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopItemDto {
    private Long itemId;
    private String itemName;
    private String category;
    private String description;
    private int gold;
    private boolean isSelected;
    private boolean isOwned;
}
