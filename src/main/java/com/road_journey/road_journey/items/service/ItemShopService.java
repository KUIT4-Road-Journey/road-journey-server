package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.repository.ItemShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ItemShopService {

    private final ItemShopRepository itemShopRepository;
    public ItemShopService(ItemShopRepository itemShopRepository) {
        this.itemShopRepository = itemShopRepository;
    }

    public List<ItemDto> getShopItems(Long userId, String category) {
        return itemShopRepository.findShopItemsByCategory(userId, category);
    }

    public int getUserGold(Long userId) {
        return itemShopRepository.getUserGold(userId);
    }

    public Map<String, Object> purchaseItem(Long userId, Long itemId) {
        int itemPrice = itemShopRepository.getItemPrice(itemId);
        int userGold = itemShopRepository.getUserGold(userId);

        if (userGold < itemPrice) {
            throw new IllegalArgumentException("골드가 부족합니다.");
        }

        int remainingGold = userGold - itemPrice;
        itemShopRepository.updateUserGold(userId, remainingGold);

        String category = itemShopRepository.getItemCategory(itemId);
        boolean isCharacter = "character".equalsIgnoreCase(category);
        itemShopRepository.purchaseItem(userId, itemId, isCharacter);

        return Map.of(
                "status", "success",
                "availableGold", remainingGold
        );
    }
}
