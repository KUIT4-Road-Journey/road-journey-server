package com.road_journey.road_journey.items.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.dto.UserItemDto;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Rollback
class ItemStorageServiceTest {

    @Autowired
    private ItemStorageService itemStorageService;

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 보유_아이템_리스트_요청_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();

        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 2000L));
        Item item = itemRepository.save(new Item(null, "Test Item", "wallpaper", "Special Description", 2000L, true));

        userItemRepository.save(new UserItem(null, user.getUserId(), item.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));


        Map<String, Object> response = itemStorageService.getUserItems(user.getUserId(), "wallpaper");


        assertEquals(1, ((List<?>) response.get("items")).size());
    }

    @Test
    void 아이템_토글_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();

        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L));
        Item item = itemRepository.save(new Item(null, "Test Item", "wallpaper", "Special Description", 2000L, true));

        UserItem userItem = userItemRepository.save(new UserItem(null, user.getUserId(), item.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));


        Map<String, Object> response = itemStorageService.toggleEquipItem(user.getUserId(), userItem.getUserItemId(), true);
        assertEquals("success", response.get("status"));
        assertEquals("Item equipped successfully.", response.get("message"));


        response = itemStorageService.toggleEquipItem(user.getUserId(), userItem.getUserItemId(), false);
        assertEquals("success", response.get("status"));
        assertEquals("Item unequipped successfully.", response.get("message"));
    }

    @Test
    void 동일_카테고리_아이템_장착시_다른_아이템_해제_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();

        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L));

        Item item1 = itemRepository.save(new Item(null, "Test Item1", "wallpaper", "Special Description1", 2000L, true));
        Item item2 = itemRepository.save(new Item(null, "Test Item2", "wallpaper", "Special Description2", 2000L, true));

        UserItem userItem1 = userItemRepository.save(new UserItem(null, user.getUserId(), item1.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));
        UserItem userItem2 = userItemRepository.save(new UserItem(null, user.getUserId(), item2.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));


        itemStorageService.toggleEquipItem(user.getUserId(), userItem2.getUserItemId(), true);


        UserItem updatedUserItem1 = userItemRepository.findById(userItem1.getUserItemId()).get();
        UserItem updatedUserItem2 = userItemRepository.findById(userItem2.getUserItemId()).get();

        assertFalse(updatedUserItem1.isSelected());
        assertTrue(updatedUserItem2.isSelected());
    }

    @Test
    void 다른_장신구_장착시_기존_장신구_유지_테스트() {
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L));

        Item item1 = itemRepository.save(new Item(null, "Test Item1", "ornament", "Special Description1", 2000L, true));
        Item item2 = itemRepository.save(new Item(null, "Test Item2", "ornament", "Special Description2", 2000L, true));

        UserItem userItem1 = userItemRepository.save(new UserItem(null, user.getUserId(), item1.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));
        UserItem userItem2 = userItemRepository.save(new UserItem(null, user.getUserId(), item2.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));


        itemStorageService.toggleEquipItem(user.getUserId(), userItem1.getUserItemId(), true);
        itemStorageService.toggleEquipItem(user.getUserId(), userItem2.getUserItemId(), true);

        UserItem updatedUserItem1 = userItemRepository.findById(userItem1.getUserItemId()).get();
        UserItem updatedUserItem2 = userItemRepository.findById(userItem2.getUserItemId()).get();


        assertTrue(updatedUserItem1.isSelected());
        assertTrue(updatedUserItem2.isSelected());
    }

    @Test
    void 사용자_장착_아이템_조회_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();

        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L));

        Item item1 = itemRepository.save(new Item(null, "Test Item1", "ornament", "Special Description1", 2000L, true));
        Item item2 = itemRepository.save(new Item(null, "Test Item2", "wallpaper", "Special Description2", 2000L, true));

        UserItem userItem1 = userItemRepository.save(new UserItem(null, user.getUserId(), item1.getItemId(), true, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));
        UserItem userItem2 = userItemRepository.save(new UserItem(null, user.getUserId(), item2.getItemId(), true, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now()));


        Map<String, Object> response = itemStorageService.getEquippedItems(user.getUserId());


        List<UserItemDto> equippedItems = (List<UserItemDto>) response.get("equippedItems");
        assertEquals(2, equippedItems.size());
        assertEquals("Test Item1", equippedItems.get(0).getItemName());
        assertEquals("Test Item2", equippedItems.get(1).getItemName());
    }
}