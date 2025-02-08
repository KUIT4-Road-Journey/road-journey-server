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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
class ItemSpecialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void 특별_아이템_조회_API_테스트() throws Exception {
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 500L, "active"));
        itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, true));

        // when & then
        mockMvc.perform(get("/items/special")
                        .param("userId", user.getUserId().toString())) //todo userId 수정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specialItems", hasSize(greaterThan(0))));
    }

    @Test
    void 특별_아이템_구매_API_테스트() throws Exception {
        User user = userRepository.save(new User("testUser", "secure_password", "test@mail.com", "nickname", 2500L, "active"));
        itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, true));

        // when & then
        mockMvc.perform(post("/items/special/order")
                        .param("userId", user.getUserId().toString())) //todo userId 수정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}