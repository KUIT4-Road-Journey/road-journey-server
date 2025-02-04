package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.items.dto.SpecialItemDto;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemSpecialService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;

    private final int SPECIAL_PRICE = 1500;

    public ItemSpecialService(ItemRepository itemRepository, UserRepository userRepository, UserItemRepository userItemRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userItemRepository = userItemRepository;
    }

    public Map<String, Object> getSpecialItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<SpecialItemDto> specialItems = itemRepository.findByIsSpecialTrue()
                .stream()
                .map(item -> new SpecialItemDto(
                        item,
                        userItemRepository.findByUserAndItemCategory(user, item.getCategory()).size() > 0
                ))
                .collect(Collectors.toList());

        return Map.of(
                "availableGold", user.getGold(),
                "specialItems", specialItems
        );
    }
}
