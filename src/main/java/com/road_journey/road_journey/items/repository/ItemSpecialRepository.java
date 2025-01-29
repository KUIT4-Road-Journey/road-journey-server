package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.dto.ItemDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemSpecialRepository {

    public List<ItemDto> findSpecialItems() {
        return List.of();
    }

    public int purchaseSpecialItem(ItemDto itemDto) {
        return 0;
    }

    public int getUserGold() {
        return 0;
    }
}
