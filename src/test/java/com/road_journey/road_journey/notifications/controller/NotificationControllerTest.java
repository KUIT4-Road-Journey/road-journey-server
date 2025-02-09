package com.road_journey.road_journey.notifications.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.CustomUserInfoDto;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String tokenUser;
    private Long testUserId;

    @BeforeEach
    public void setup() {
        User testUser = userRepository.save(new User("testUser", "password123", "test@example.com", "TestNickname", 100L, "active"));
        testUserId = testUser.getUserId();
        tokenUser = "Bearer " + jwtUtil.createAccessToken(
                new CustomUserInfoDto(testUserId, "testUser", "password123", "test@example.com", "TestNickname", "ROLE_USER"));
    }

    @Test
    public void 알림_조회_테스트() throws Exception {
        notificationRepository.save(new Notification(testUserId, "NOTIFICATION", 100L, "Test message"));

        mockMvc.perform(get("/notifications")
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].message").value("Test message"));
    }

    @Test
    public void 알림_삭제_테스트() throws Exception {
        Notification notification = notificationRepository.save(new Notification(testUserId, "NOTIFICATION", 100L, "Test message"));

        mockMvc.perform(delete("/notifications/" + notification.getNotificationId())
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void 전체_알림_삭제_테스트() throws Exception {
        notificationRepository.save(new Notification(testUserId, "NOTIFICATION", 100L, "Test message100"));
        notificationRepository.save(new Notification(testUserId, "NOTIFICATION", 101L, "Test message101"));

        mockMvc.perform(delete("/notifications")
                        .header("Authorization", tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}