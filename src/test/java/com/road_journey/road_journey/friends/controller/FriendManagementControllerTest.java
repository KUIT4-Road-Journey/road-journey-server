package com.road_journey.road_journey.friends.controller;

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
public class FriendManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    private Long userId1;
    private Long userId2;
    private Long friendId;

    @BeforeEach
    public void setUp() {
        User user1 = new User("user1", "password1", "user1@test.com", "User One", 0L, "active");
        User user2 = new User("user2", "password2", "user2@test.com", "User Two", 0L, "active");

        userRepository.save(user1);
        userRepository.save(user2);

        userId1 = user1.getUserId();
        userId2 = user2.getUserId();

        friendId = friendRepository.save(new Friend(userId1, userId2, true, "IS_FRIEND")).getFriendId();
    }

    @Test
    public void 친구목록_조회_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends")
                        .param("userId", String.valueOf(userId1))
                        .param("sortBy", "alphabetical")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].accountId").value("user2"));
    }

    @Test
    public void 친구메인_정보_조회_성공() throws Exception {
        // todo
    }

    @Test
    public void 친구좋아요_상태변경_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/friends/" + friendId + "/likes")
                        .param("userId", String.valueOf(userId1))
                        .content("{\"action\": true}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Friend like status updated."));
    }

    @Test
    public void 친구목록_조회_잘못된_userId_실패() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends")
                        .param("userId", "9999")  // 존재하지 않는 userId
                        .param("sortBy", "alphabetical")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));  // 결과가 빈 리스트인지 확인
    }
}