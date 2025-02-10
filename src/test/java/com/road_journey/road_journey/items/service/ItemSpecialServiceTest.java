package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Rollback
public class ItemSpecialServiceTest {

    @Autowired
    private ItemSpecialService itemSpecialService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    @Test
    void 특별_아이템_구매_성공_테스트() {
        itemRepository.deleteAll();
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 40000L));
        System.out.println("Created User ID: " + user.getUserId());

        Item specialItem = itemRepository.save(new Item(null, "Special Item", "character", "Special Description", 2000L, true));
        System.out.println("Created Item ID: " + specialItem.getItemId());


        Map<String, Object> response = itemSpecialService.purchaseSpecialItem(user.getUserId());


        Assertions.assertEquals("success", response.get("status"));
        Assertions.assertEquals(10000L, response.get("availableGold"));
        Assertions.assertTrue(userItemRepository.existsByUserIdAndItemId(user.getUserId(), specialItem.getItemId()));
    }

    @Test
    void 중복_특별_아이템_구매_실패_골드차감_테스트() {
        itemRepository.deleteAll();
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 40000L));
        System.out.println("Created User ID: " + user.getUserId());

        Item specialItem = itemRepository.save(new Item(null, "Special Item", "character", "Special Description", 2000L, true));
        System.out.println("Created Item ID: " + specialItem.getItemId());

        userItemRepository.save(UserItem.builder().userId(user.getUserId()).itemId(specialItem.getItemId()).isSelected(false).growthPoint(0L).growthLevel(1L).status("active").build());


        Map<String, Object> response = itemSpecialService.purchaseSpecialItem(user.getUserId());


        Assertions.assertEquals("success", response.get("status"));
        Assertions.assertEquals(10000L, response.get("availableGold"));  // 골드가 차감되어야 함
        long itemCount = userItemRepository.count();  // 유저 아이템 개수 확인
        Assertions.assertEquals(1, itemCount);  // 새로운 아이템이 추가되지 않아야 함
    }
}