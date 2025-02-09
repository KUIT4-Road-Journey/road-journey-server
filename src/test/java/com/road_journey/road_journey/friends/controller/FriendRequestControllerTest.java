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
public class FriendRequestControllerTest {

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

        friendId = friendRepository.save(new Friend(userId1, userId2, false, "PENDING"))
                            .getFriendId();
    }

    @Test
    public void 받은_친구요청_목록_조회_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/friends/requests")
                        .param("userId", String.valueOf(userId2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].accountId").value("user1"))
                .andExpect(jsonPath("$[0].friendStatus").value("PENDING"));
    }

    @Test
    public void 친구요청_보내기_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/friends/requests")
                        .param("userId", String.valueOf(userId2))
                        .content("{\"friendUserId\": " + userId1 + "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Friend request sent successfully."));
    }

    @Test
    public void 친구요청_수락_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/friends/requests/" + friendId + "/action")
                        .param("userId", String.valueOf(userId2))
                        .content("{\"action\": \"accept\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Friend request accepted."));
    }

    @Test
    public void 친구요청_거절_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/friends/requests/" + friendId + "/action")
                        .param("userId", String.valueOf(userId2))
                        .content("{\"action\": \"reject\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Friend request rejected."));
    }

    @Test
    public void 친구요청_잘못된_액션_실패() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/friends/requests/" + friendId + "/action")
                        .param("userId", String.valueOf(userId2))
                        .content("{\"action\": \"invalid_action\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid action."));
    }
}