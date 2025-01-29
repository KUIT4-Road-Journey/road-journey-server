package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.UserDetail;
import com.road_journey.road_journey.items.dto.ShopItemDto;
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

    public List<ShopItemDto> getShopItems(UserDetail userDetail, String category) {
        return itemShopRepository.findShopItemsByCategory(userDetail.getUserId(), category);
    }

    public int getUserGold(UserDetail userDetail) {
        return itemShopRepository.getUserGold(userDetail.getUserId());
    }

    public Map<String, Object> purchaseItem(UserDetail userDetail, int itemId) {
        int itemPrice = itemShopRepository.getItemPrice(itemId);
        int userGold = itemShopRepository.getUserGold(userDetail.getUserId());

        if (userGold < itemPrice) {
            throw new IllegalArgumentException("골드가 부족합니다.");
        }

        int remainingGold = userGold - itemPrice;
        itemShopRepository.updateUserGold(userDetail.getUserId(), remainingGold);

        String category = itemShopRepository.getItemCategory(itemId);
        boolean isCharacter = "character".equalsIgnoreCase(category);
        itemShopRepository.purchaseItem(userDetail.getUserId(), itemId, isCharacter);

        return Map.of(
                "status", "success",
                "availableGold", remainingGold
        );
    }
}
