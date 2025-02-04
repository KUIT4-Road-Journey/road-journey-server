package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.items.dto.UserItemDto;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemStorageService {

    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    public ItemStorageService(UserItemRepository userItemRepository, UserRepository userRepository) {
        this.userItemRepository = userItemRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> getUserItems(Long userId, String category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<UserItemDto> items = userItemRepository.findByUserAndItemCategory(user, category)
                .stream()
                .map(userItem -> new UserItemDto(userItem))  // 직접 생성자 호출
                .collect(Collectors.toList());

        return Map.of("items", items);
    }

    @Transactional
    public Map<String, Object> toggleEquipItem(Long userId, Long userItemId, boolean isEquipped) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        UserItem userItem = userItemRepository.findById(userItemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템 없음"));

        if (isEquipped) {
            unequipSameCategoryItems(user, userItem.getItem().getCategory());
        }

        userItem.setSelected(isEquipped);
        userItemRepository.save(userItem);

        return Map.of(
                "status", "success",
                "message", isEquipped ? "아이템 장착 완료." : "아이템 장착 해제 완료."
        );
    }

    private void unequipSameCategoryItems(User user, String category) {
        List<UserItem> sameCategoryItems = userItemRepository.findByUserAndItemCategory(user, category);
        sameCategoryItems.forEach(item -> item.setSelected(false));
        userItemRepository.saveAll(sameCategoryItems);
    }
}
