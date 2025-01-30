package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.items.dto.UserItemDto;
import com.road_journey.road_journey.items.repository.ItemStorageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ItemStorageService {

    private final ItemStorageRepository itemStorageRepository;

    public ItemStorageService(ItemStorageRepository itemStorageRepository) {
        this.itemStorageRepository = itemStorageRepository;
    }

    public Map<String, Object> getUserItems(Long userId, String category) {
        List<UserItemDto> items = itemStorageRepository.findUserItems(userId, category);

        return Map.of("items", items);
    }

    @Transactional
    public Map<String, Object> toggleEquipItem(Long userId, Long userItemId, boolean isEquipped) {
        allUnequipIfNotOrnament(userId, userItemId);

        itemStorageRepository.updateItemEquippedStatus(userId, userItemId, isEquipped);

        return Map.of(
                "status", "success",
                "message", isEquipped ? "Item equipped successfully." : "Item unequipped successfully."
        );
    }

    private void allUnequipIfNotOrnament(Long userId, Long userItemId) {
        String category = itemStorageRepository.getItemCategory(userItemId);
        if (!"ornament".equalsIgnoreCase(category)) {
            itemStorageRepository.unequipSameCategoryItems(userId, category);
        }
    }
}
