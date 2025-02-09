package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import com.road_journey.road_journey.notifications.service.NotificationService;
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
public class FriendMessagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId1;
    private Long userId2;
    private Long friendId;
    private Long notificationId;

    @BeforeEach
    public void setUp() {
        userId1 = userRepository.save(new User("user1", "password1", "user1@test.com", "User One", 0L, "active"))
                        .getUserId();
        userId2 = userRepository.save(new User("user2", "password2", "user2@test.com", "User Two", 0L, "active"))
                .getUserId();

        friendId = friendRepository.save(new Friend(userId1, userId2, true, "IS_FRIEND")).getFriendId();

        Notification notification = new Notification(userId1, "friend_request", friendId, "친구 요청이 있습니다.");
        notificationId = notificationRepository.save(notification).getNotificationId();
    }

    @Test
    public void 받은메시지_목록_조회_성공() throws Exception {
        // todo
    }

    @Test
    public void 개별메시지_삭제_성공() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/friends/messages/" + notificationId)
                        .param("userId", String.valueOf(userId1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Notification deleted."));
    }

    @Test
    public void 개별메시지_삭제_존재하지않는_메시지() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/friends/messages/999999")
                        .param("userId", String.valueOf(userId1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Notification not found or does not belong to the user."));
    }
}