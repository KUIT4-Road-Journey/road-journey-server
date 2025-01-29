package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.repository.ItemStorageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemStorageService {
    private final ItemStorageRepository itemStorageRepository;

    public ItemStorageService(ItemStorageRepository itemStorageRepository) {
        this.itemStorageRepository = itemStorageRepository;
    }

    public List<ItemDto> getUserItems(String category) {
        return itemStorageRepository.findUserItemsByCategory(category);
    }

    public void equipItem(Long userItemId, boolean isEquipped) {
        itemStorageRepository.updateItemEquipStatus(userItemId, isEquipped);
    }
}
