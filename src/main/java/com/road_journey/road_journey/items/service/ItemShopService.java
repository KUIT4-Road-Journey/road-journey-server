package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemShopService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;

    public ItemShopService(ItemRepository itemRepository, UserRepository userRepository, UserItemRepository userItemRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userItemRepository = userItemRepository;
    }

    public List<ItemDto> getShopItems(String category) {
        List<Item> items = category.equals("all")
                ? itemRepository.findAll()
                : itemRepository.findByCategoryAndIsSpecialFalse(category);

        return items.stream()
                .map(item -> new ItemDto(item))
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> purchaseItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("아이템 없음"));

        if (user.getGold() < item.getGold()) {
            return Map.of(
                    "status", "failed",
                    "message", "골드가 부족합니다.",
                    "availableGold", user.getGold()
            );
        }

        // 골드 차감 후 저장
        user.setGold(user.getGold() - item.getGold());
        userRepository.save(user);

        // 새로운 `UserItem` 생성 및 저장
        UserItem userItem = new UserItem();
        userItem.setUserId(userId);
        userItem.setItemId(itemId);
        userItem.setSelected(false);
        userItem.setStatus("active");
        userItem.setCreatedAt(LocalDateTime.now());

        // 선택된 아이템이 "character" 카테고리인 경우 → growthPoint=0, growthLevel=1
        if ("character".equalsIgnoreCase(item.getCategory())) {
            userItem.setGrowthPoint(0L);
            userItem.setGrowthLevel(1L);
        } else {
            userItem.setGrowthPoint(null);
            userItem.setGrowthLevel(null);
        }

        userItemRepository.save(userItem);

        return Map.of(
                "status", "success",
                "availableGold", user.getGold(),
                "purchasedItemId", itemId
        );
    }
}
