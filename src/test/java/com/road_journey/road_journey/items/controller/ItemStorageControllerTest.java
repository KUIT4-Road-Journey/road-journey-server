package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
class ItemStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String tokenUser;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(new User("user1", "password1", "user1@test.com", "User One", 0L));
        tokenUser = "Bearer " + jwtUtil.createAccessToken(
                new CustomUserInfoDto(user.getUserId(), "user1", "password1", "user1@test.com", "User One", "USER"));
    }

    @Test
    void 보유_아이템_조회_API_테스트() throws Exception {
        User user = userRepository.findByAccountId("user1").orElseThrow();
        Item item = itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        UserItem userItem = new UserItem(null, user.getUserId(), item.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now());
        userItemRepository.save(userItem);

        // when & then
        mockMvc.perform(get("/items/storage")
                        .header("Authorization", tokenUser)
                        .param("category", "wallpaper"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(greaterThan(0))));
    }

    @Test
    void 아이템_장착_API_테스트() throws Exception {
        User user = userRepository.findByAccountId("user1").orElseThrow();
        Item item = itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        UserItem userItem = new UserItem(null, user.getUserId(), item.getItemId(), false, 0L, 1L, "active", LocalDateTime.now(), LocalDateTime.now());
        userItem = userItemRepository.save(userItem);

        // when & then
        mockMvc.perform(patch("/items/storage/" + userItem.getUserItemId() + "/equip")
                        .header("Authorization", tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isEquipped\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}