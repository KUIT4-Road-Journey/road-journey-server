package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
class ItemShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void 아이템_상점_아이템_조회_API_테스트() throws Exception {
        // given
        itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        itemRepository.save(new Item(null, "노을", "wallpaper", "해질녘 노을...", 2500L, false));

        // when & then
        mockMvc.perform(get("/items/shop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void 아이템_구매_API_테스트() throws Exception {
        // given
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L, "active"));
        Item item = itemRepository.save(new Item(null, "Sword", "weapon", "A sharp sword", 100L, false));

        // when & then
        mockMvc.perform(post("/items/shop/order")
                        .param("userId", user.getUserId().toString())
                        .param("itemId", item.getItemId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}