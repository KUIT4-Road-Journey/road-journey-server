package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.dto.ItemDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemShopRepository {

    public List<ItemDto> findShopItemsByCategory(String category) {
        return List.of();
    }

    public int purchaseItem(ItemDto itemDto) {
        return 0;
    }

    public int getUserGold() {
        return 0;
    }
}
