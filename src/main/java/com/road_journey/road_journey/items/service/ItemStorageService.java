package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.items.dto.UserItemDto;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemStorageService {

    private final UserItemRepository userItemRepository;
    private final ItemRepository itemRepository;

    public ItemStorageService(UserItemRepository userItemRepository, ItemRepository itemRepository) {
        this.userItemRepository = userItemRepository;
        this.itemRepository = itemRepository;
    }

    public Map<String, Object> getUserItems(Long userId, String category) {
        List<UserItemDto> items;

        if ("all".equalsIgnoreCase(category)) {
            // 모든 userItem 검색
            items = userItemRepository.findByUserId(userId)
                    .stream()
                    .map(userItem -> {
                        Item item = itemRepository.findById(userItem.getItemId())
                                .orElseThrow(() -> new IllegalArgumentException("아이템 정보 없음"));
                        return new UserItemDto(userItem, item);
                    })
                    .collect(Collectors.toList());
        } else {
            // 특정 category의 userItem 검색
            items = userItemRepository.findByUserIdAndCategory(userId, category)
                    .stream()
                    .map(userItem -> {
                        Item item = itemRepository.findById(userItem.getItemId())
                                .orElseThrow(() -> new IllegalArgumentException("아이템 정보 없음"));
                        return new UserItemDto(userItem, item);
                    })
                    .collect(Collectors.toList());
        }

        return Map.of("items", items);
    }

    @Transactional
    public Map<String, Object> toggleEquipItem(Long userId, Long userItemId, boolean isEquipped) {
        UserItem userItem = userItemRepository.findById(userItemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템 없음"));

        Item item = itemRepository.findById(userItem.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("아이템 정보 없음"));

        if (!"ornament".equalsIgnoreCase(item.getCategory()) && isEquipped) {
            unequipSameCategoryItems(userId, item.getCategory());
        }

        userItem.setSelected(isEquipped);
        userItemRepository.save(userItem);

        return Map.of(
                "status", "success",
                "message", isEquipped ? "Item equipped successfully." : "Item unequipped successfully."
        );
    }

    private void unequipSameCategoryItems(Long userId, String category) {
        List<UserItem> sameCategoryItems = userItemRepository.findByUserIdAndCategory(userId, category);
        sameCategoryItems.forEach(item -> item.setSelected(false));
        userItemRepository.saveAll(sameCategoryItems);
    }

    public Map<String, Object> getEquippedItems(Long userId) {
        List<UserItem> equippedUserItems = userItemRepository.findByUserIdAndIsSelectedTrue(userId);

        List<UserItemDto> equippedItems = equippedUserItems.stream()
                .map(userItem -> {
                    Item item = itemRepository.findById(userItem.getItemId())
                            .orElseThrow(() -> new IllegalArgumentException("아이템 정보 없음"));
                    return new UserItemDto(userItem, item);
                })
                .collect(Collectors.toList());

        return Map.of("equippedItems", equippedItems);
    }
}
