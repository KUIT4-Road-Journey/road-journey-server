package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.items.dto.SpecialItemDto;
import com.road_journey.road_journey.items.repository.ItemShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemSpecialService {
    private final ItemShopRepository itemShopRepository;  //todo my 리포 역할 임시 책임
    private final int SPECIALPRICE = 1500;

    public ItemSpecialService(ItemShopRepository itemShopRepository) {
        this.itemShopRepository = itemShopRepository;
    }

    public Map<String, Object> getSpecialItems(Long userId) {
        int userGold = itemShopRepository.getUserGold(userId);
        List<SpecialItemDto> specialItems = itemShopRepository.findSpecialItems(userId);

        return Map.of(
                "availableGold", userGold,
                "specialItems", specialItems
        );
    }

    public Map<String, Object> purchaseSpecialItem(Long userId) {
        int userGold = itemShopRepository.getUserGold(userId);
        if (userGold < SPECIALPRICE) {
            return Map.of(
                    "status", "failed",
                    "message", "골드가 부족합니다.",
                    "availableGold", userGold
            );
        }

        Optional<Integer> optionalItemId = itemShopRepository.getRandomSpecialItemId(userId);
        if (optionalItemId.isEmpty()) {
            return Map.of(
                    "status", "failed",
                    "message", "모든 특별 아이템을 보유 중입니다.",
                    "availableGold", userGold
            );
        }

        Long itemId = optionalItemId.get().longValue();
        String category = itemShopRepository.getItemCategory(itemId);
        boolean isCharacter = "character".equalsIgnoreCase(category);

        int remainingGold = userGold - SPECIALPRICE;
        itemShopRepository.updateUserGold(userId, remainingGold);

        itemShopRepository.purchaseItem(userId, itemId, isCharacter);

        return Map.of(
                "status", "success",
                "availableGold", remainingGold,
                "selectedItemId", itemId
        );
    }
}
