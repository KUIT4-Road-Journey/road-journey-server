package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.items.entity.Item;
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
public class ItemShopServiceTest {

    @Autowired
    private ItemShopService itemShopService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    @Test
    @Transactional
    void 아이템_구매_성공_테스트() {
        User user = new User("testUser", "secure_password", "test@mail.com", "nickname", 1500L, "active");
        user = userRepository.save(user);
        System.out.println("Created User ID: " + user.getUserId());

        Item item = new Item(null, "Test Item", "background", "Test Description", 1000L, false);
        item = itemRepository.save(item);
        System.out.println("Created Item ID: " + item.getItemId());


        Map<String, Object> response = itemShopService.purchaseItem(user.getUserId(), item.getItemId());


        Assertions.assertEquals("success", response.get("status"));
        Assertions.assertEquals(500L, response.get("availableGold"));
        Assertions.assertTrue(userItemRepository.existsByUserIdAndItemId(user.getUserId(), item.getItemId()));
    }

    @Test
    @Transactional
    void 아이템_구매_실패_테스트() {
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L, "active"));
        System.out.println("Created User ID: " + user.getUserId());

        Item item = itemRepository.save(new Item(null, "Test Item", "background", "Test Description", 1000L, false));
        System.out.println("Created Item ID: " + item.getItemId());


        Map<String, Object> response = itemShopService.purchaseItem(user.getUserId(), item.getItemId());


        Assertions.assertEquals("failed", response.get("status"));
        Assertions.assertEquals(500L, response.get("availableGold"));
        Assertions.assertFalse(userItemRepository.existsByUserIdAndItemId(user.getUserId(), item.getItemId()));
    }
}