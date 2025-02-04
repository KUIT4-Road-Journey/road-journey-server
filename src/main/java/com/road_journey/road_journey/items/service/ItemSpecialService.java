package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.items.dto.SpecialItemDto;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
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

    @Transactional
    public Map<String, Object> purchaseSpecialItem(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        if (user.getGold() < SPECIAL_PRICE) {
            return Map.of(
                    "status", "failed",
                    "message", "골드가 부족합니다.",
                    "availableGold", user.getGold()
            );
        }

        // 사용자가 보유하지 않은 특별 아이템 조회
        List<Item> availableSpecialItems = itemRepository.findByIsSpecialTrue();
        availableSpecialItems.removeIf(item -> userItemRepository.findByUserAndItemCategory(user, item.getCategory()).size() > 0);

        if (availableSpecialItems.isEmpty()) {
            return Map.of(
                    "status", "failed",
                    "message", "모든 특별 아이템을 보유 중입니다.",
                    "availableGold", user.getGold()
            );
        }

        // 랜덤한 특별 아이템 선택
        Item selectedItem = availableSpecialItems.get(new Random().nextInt(availableSpecialItems.size()));

        // 골드 차감 후 저장
        user.setGold(user.getGold() - SPECIAL_PRICE);
        userRepository.save(user);

        // 특별 아이템 지급
        UserItem userItem = new UserItem();
        userItem.setUser(user);
        userItem.setItem(selectedItem);
        userItemRepository.save(userItem);

        return Map.of(
                "status", "success",
                "availableGold", user.getGold(),
                "selectedItemId", selectedItem.getItemId()
        );
    }
}
