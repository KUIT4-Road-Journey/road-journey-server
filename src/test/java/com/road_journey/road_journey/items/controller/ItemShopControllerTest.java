package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.entity.Item;
import com.road_journey.road_journey.items.repository.ItemRepository;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String tokenUser;
    private Long userId;

    @BeforeEach
    void setUp() {
        userItemRepository.deleteAll();
        itemRepository.deleteAll();

        User user = userRepository.save(new User("user1", "password1", "user1@test.com", "User One", 40000L));
        userId = user.getUserId();
        tokenUser = "Bearer " + jwtUtil.createAccessToken(
                new CustomUserInfoDto(userId, "user1", "password1", "user1@test.com", "User One", "USER"));
    }

    @Test
    void 아이템_상점_아이템_조회_API_테스트() throws Exception {
        itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));
        itemRepository.save(new Item(null, "노을", "wallpaper", "해질녘 노을...", 2500L, false));
        itemRepository.save(new Item(null, "Test Item", "ornament", "Special Description", 2000L, false));


        mockMvc.perform(get("/items/shop")
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(org.hamcrest.Matchers.greaterThan(0)));

        mockMvc.perform(get("/items/shop")
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableGold").value(40000))
                .andExpect(jsonPath("$.items[0].itemName").value("밤하늘"))
                .andExpect(jsonPath("$.items[1].itemName").value("노을"));

        mockMvc.perform(get("/items/shop")
                        .header("Authorization", tokenUser)
                        .param("category", "ornament"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.size()").value(1))
                .andExpect(jsonPath("$.items[0].itemName").value("Test Item"));
    }

    @Test
    void 아이템_구매_API_테스트() throws Exception {
        Item item = itemRepository.save(new Item(null, "밤하늘", "wallpaper", "암흑 공간을 수놓은 반짝거리는 ...", 2500L, false));


        mockMvc.perform(post("/items/shop/order")
                        .header("Authorization", tokenUser)
                        .param("itemId", item.getItemId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}