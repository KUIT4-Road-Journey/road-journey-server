package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.repository.ItemShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemShopService {
    private final ItemShopRepository itemShopRepository;

    public ItemShopService(ItemShopRepository itemShopRepository) {
        this.itemShopRepository = itemShopRepository;
    }

    public List<ItemDto> getShopItems(String category) {
        return itemShopRepository.findShopItemsByCategory(category);
    }

    public int purchaseItem(ItemDto itemDto) {
        return itemShopRepository.purchaseItem(itemDto);
    }

    public int getUserGold() {
        return itemShopRepository.getUserGold();
    }
}
