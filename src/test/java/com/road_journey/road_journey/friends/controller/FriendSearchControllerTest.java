package com.road_journey.road_journey.friends.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
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
    private ObjectMapper objectMapper;

    private Long userId1;
    private Long userId2;

    @BeforeEach
    public void setUp() {
        User user1 = new User("user1", "password1", "user1@test.com", "User One", 0L, "active");
        User user2 = new User("user2", "password2", "user2@test.com", "User Two", 0L, "active");

        userRepository.save(user1);
        userRepository.save(user2);

        userId1 = user1.getUserId();
        userId2 = user2.getUserId();

        friendRepository.save(new Friend(userId1, userId2, false, "PENDING"));
    }

    @Test
    public void 친구검색_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends/search")
                        .param("userId", String.valueOf(userId1))
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
                        .param("userId", String.valueOf(userId1))
                        .param("searchId", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    public void 친구검색_userId_누락_실패() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends/search")
                        .param("searchId", "user2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}