package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.repository.ItemSpecialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemSpecialService {
    private final ItemSpecialRepository itemSpecialRepository;

    public ItemSpecialService(ItemSpecialRepository itemSpecialRepository) {
        this.itemSpecialRepository = itemSpecialRepository;
    }

    public List<ItemDto> getSpecialItems() {
        return itemSpecialRepository.findSpecialItems();
    }

    public int purchaseSpecialItem(ItemDto itemDto) {
        return itemSpecialRepository.purchaseSpecialItem(itemDto);
    }

    public int getUserGold() {
        return itemSpecialRepository.getUserGold();
    }
}
