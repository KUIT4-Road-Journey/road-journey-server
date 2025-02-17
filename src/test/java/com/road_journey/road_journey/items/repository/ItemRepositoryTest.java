package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.items.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Rollback
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserItemRepository userItemRepository;

    @Test
    void 아이템_저장_테스트() {
        Item item = new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false);


        Item savedItem = itemRepository.save(item);


        assertNotNull(savedItem.getItemId());
        assertEquals("밤하늘", savedItem.getItemName());
        assertEquals("wallpaper", savedItem.getCategory());
    }

    @Test
    void 카테고리별_아이템_조회_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();
        itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        itemRepository.save(new Item(null, "노을", "wallpaper", "해질녘 노을...", 2500L, false));
        List<Item> armorItems = itemRepository.findByCategory("wallpaper");


        assertEquals(2, armorItems.size());
    }

    @Test
    void 특별_아이템_조회_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();
        itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, true));
        List<Item> specialItems = itemRepository.findByIsSpecialTrue();


        assertEquals(1, specialItems.size());
    }

    @Test
    void findRandomItemsByCategory_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();
        itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        itemRepository.save(new Item(null, "노을", "wallpaper", "해질녘 노을 ...", 2500L, false));
        itemRepository.save(new Item(null, "새벽하늘", "wallpaper", "아침 새벽의 떠오르는 태양 ...", 2500L, false));
        List<Item> randomItems = itemRepository.findRandomItemsByCategory("wallpaper", 2);

        assertEquals(2, randomItems.size());
        randomItems.forEach(item -> assertEquals("wallpaper", item.getCategory()));
    }
}