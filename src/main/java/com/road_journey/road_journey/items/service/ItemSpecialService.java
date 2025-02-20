package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.dto.SpecialItemDto;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ItemSpecialService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;

    private static final int SPECIAL_PRICE = 30000;

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
                        userItemRepository.findByUserIdAndCategory(userId, item.getCategory()).size() > 0
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

        // 모든 특별 아이템 조회
        List<Item> specialItems = itemRepository.findByIsSpecialTrue();
        if (specialItems.isEmpty()) {
            return Map.of(
                    "status", "failed",
                    "message", "구매 가능한 특별 아이템이 없습니다.",
                    "availableGold", user.getGold()
            );
        }

        // 랜덤한 특별 아이템 선택
        Item selectedItem = specialItems.get(new Random().nextInt(specialItems.size()));

        // 골드 차감 후 저장
        user.setGold(user.getGold() - SPECIAL_PRICE);
        userRepository.save(user);

        // 해당 아이템을 이미 보유한 경우 → 골드만 차감 (아이템 지급 없음)
        boolean alreadyOwned = userItemRepository.existsByUserIdAndItemId(userId, selectedItem.getItemId());

        if (alreadyOwned) {
            return Map.of(
                    "status", "success",
                    "message", "이미 보유한 아이템이 선택되었습니다. 골드만 차감됩니다.",
                    "availableGold", user.getGold(),
                    "selectedItemId", selectedItem.getItemId(),
                    "itemName", selectedItem.getItemName(),
                    "description", selectedItem.getDescription()
            );
        }

        // 보유하지 않은 경우 → 아이템 지급
        UserItem userItem = new UserItem();
        userItem.setUserId(userId);
        userItem.setItemId(selectedItem.getItemId());
        userItem.setSelected(false);
        userItem.setStatus("active");
        userItem.setCreatedAt(LocalDateTime.now());

        // 선택된 아이템이 "character" 카테고리인 경우 growthPoint=0, growthLevel=1
        if ("character".equalsIgnoreCase(selectedItem.getCategory())) {
            userItem.setGrowthPoint(0L);
            userItem.setGrowthLevel(1L);
        } else {
            userItem.setGrowthPoint(null);
            userItem.setGrowthLevel(null);
        }

        userItemRepository.save(userItem);

        return Map.of(
                "status", "success",
                "message", "새로운 특별 아이템이 지급되었습니다.",
                "availableGold", user.getGold(),
                "selectedItemId", selectedItem.getItemId(),
                "itemName", selectedItem.getItemName(),
                "description", selectedItem.getDescription()
        );
    }



    @Scheduled(cron = "0 0 0 */14 * ?")  // 2주마다 자정에 실행
    public void updateSpecialItems() {
        resetAllSpecialItems();
        updateRandomSpecialItems();
    }

    @Transactional
    public void resetAllSpecialItems() {
        List<Item> currentSpecialItems = itemRepository.findByIsSpecialTrue();
        currentSpecialItems.forEach(item -> item.setSpecial(false));
        itemRepository.saveAll(currentSpecialItems);
    }

    @Transactional
    public void updateRandomSpecialItems() {
        setRandomSpecialItems("character", 2);
        setRandomSpecialItems("ornament", 3);
        setRandomSpecialItems("wallpaper", 3);
    }

    private void setRandomSpecialItems(String category, int count) {
        List<Item> randomItems = itemRepository.findRandomItemsByCategory(category, count);
        randomItems.forEach(item -> item.setSpecial(true));
        itemRepository.saveAll(randomItems);
    }
}
