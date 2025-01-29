package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.dto.ItemDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemStorageRepository {

    public List<ItemDto> findUserItemsByCategory(String category) {
        return List.of();
    }

    public void updateItemEquipStatus(Long userItemId, boolean isEquipped) {
    }
}

