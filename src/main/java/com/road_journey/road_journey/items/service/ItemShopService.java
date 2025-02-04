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
            throw new IllegalArgumentException("골드 부족");
        }

        user.setGold(user.getGold() - item.getGold());
        userRepository.save(user);

        UserItem userItem = new UserItem();
        userItem.setUser(user);
        userItem.setItem(item);
        userItemRepository.save(userItem);

        return Map.of("status", "success", "availableGold", user.getGold());
    }
}
