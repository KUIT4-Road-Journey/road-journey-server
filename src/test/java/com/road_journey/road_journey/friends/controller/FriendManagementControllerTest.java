package com.road_journey.road_journey.friends.controller;

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
public class FriendManagementControllerTest {

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
        friendId = friendRepository.save(new Friend(userId1, userId2, true, "IS_FRIEND")).getFriendId();

        tokenUser1 = "Bearer " + jwtUtil.createAccessToken(
                new CustomUserInfoDto(userId1, "user1", "password1", "user1@test.com", "User One", "USER"));
    }

    @Test
    public void 친구목록_조회_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends")
                        .header("Authorization", tokenUser1)
                        .param("sortBy", "alphabetical")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friends.size()").value(1))
                .andExpect(jsonPath("$.friends[0].accountId").value("user2"));
    }

    @Test
    public void 친구메인_정보_조회_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends/" + friendId + "/main")
                        .header("Authorization", tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void 친구좋아요_상태변경_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/friends/" + friendId + "/likes")
                        .header("Authorization", tokenUser1)
                        .content("{\"action\": true}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Friend like status updated."));
    }
}