package com.road_journey.road_journey.items.dto;

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
}
