package com.road_journey.road_journey.items.repository;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
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
class UserItemRepositoryTest {

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_아이템_저장_테스트() {
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L));
        Item item = itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        UserItem userItem = UserItem.builder().userId(user.getUserId()).itemId(item.getItemId()).isSelected(false).growthPoint(0L).growthLevel(1L).status("active").build();


        UserItem savedUserItem = userItemRepository.save(userItem);


        assertNotNull(savedUserItem.getUserItemId());
        assertEquals(user.getUserId(), savedUserItem.getUserId());
        assertEquals(item.getItemId(), savedUserItem.getItemId());
    }

    @Test
    void 사용자가_보유한_아이템_조회_테스트() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L));

        Item item1 = itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        Item item2 = itemRepository.save(new Item(null, "노을", "wallpaper", "해질녘 노을...", 150L, false));

        userItemRepository.save(UserItem.builder().userId(user.getUserId()).itemId(item1.getItemId()).isSelected(false).growthPoint(0L).growthLevel(1L).status("active").build());
        userItemRepository.save(UserItem.builder().userId(user.getUserId()).itemId(item2.getItemId()).isSelected(false).growthPoint(0L).growthLevel(1L).status("active").build());


        List<UserItem> armorItems = userItemRepository.findByUserIdAndCategory(user.getUserId(), "wallpaper");


        assertEquals(2, armorItems.size());
    }
}