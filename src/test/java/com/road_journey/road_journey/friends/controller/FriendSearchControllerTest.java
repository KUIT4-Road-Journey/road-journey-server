package com.road_journey.road_journey.friends.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Rollback
public class FriendSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String tokenUser1;
    private Long userId1;
    private Long userId2;
    private Long friendId;

    @BeforeEach
    public void setUp() {
        User user1 = new User("user1", "password1", "user1@test.com", "User One", 0L);
        User user2 = new User("user2", "password2", "user2@test.com", "User Two", 0L);

        userRepository.save(user1);
        userRepository.save(user2);

        userId1 = user1.getUserId();
        userId2 = user2.getUserId();
        friendId = friendRepository.save(new Friend(userId1, userId2, false, "PENDING")).getFriendId();

        tokenUser1 = "Bearer " + jwtUtil.createAccessToken(
                new CustomUserInfoDto(userId1, "user1", "password1", "user1@test.com", "User One", "USER"));
    }

    @Test
    public void 친구검색_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends/search")
                        .header("Authorization", tokenUser1)
                        .param("searchId", "user2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].accountId").value("user2"))
                .andExpect(jsonPath("$[0].friendStatus").value("PENDING"));
    }

    @Test
    public void 친구검색_결과없음() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends/search")
                        .header("Authorization", tokenUser1)
                        .param("searchId", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}