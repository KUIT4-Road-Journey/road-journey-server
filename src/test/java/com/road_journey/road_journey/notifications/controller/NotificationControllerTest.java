package com.road_journey.road_journey.notifications.controller;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    private Long testUserId;  //테스트용 사용자 ID 저장

    @BeforeEach
    public void setup() {
        User testUser = new User("testUser", "password123", "test@example.com", "TestNickname", 100L, "ACTIVE");
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.refresh(testUser);
        testUserId = testUser.getUserId();
//        System.out.println("[TEST] 테스트 사용자 ID: " + testUserId);
    }

    @Test
    public void 알림_조회_테스트() throws Exception {
        Notification notification = new Notification(testUserId, "test_category", 100L, "Test message");
        notificationRepository.save(notification);
        entityManager.flush();
        entityManager.refresh(notification);

//        System.out.println("[TEST] 저장된 알림 ID: " + notification.getNotificationId());
//        System.out.println("[TEST] 저장된 알림 userId: " + notification.getUserId());


        mockMvc.perform(get("/notifications")
                        .param("userId", String.valueOf(testUserId))
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].message").value("Test message"));
    }

    @Test
    public void 알림_삭제_테스트() throws Exception {
        Notification notification = new Notification(testUserId, "test_category", 100L, "Test message");
        notificationRepository.save(notification);
        entityManager.flush();
        entityManager.refresh(notification);

//        System.out.println("[TEST] 삭제할 알림 ID: " + notification.getNotificationId());


        mockMvc.perform(delete("/notifications/" + notification.getNotificationId())
                        .param("userId", String.valueOf(testUserId))
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void 전체_알림_삭제_테스트() throws Exception {
        notificationRepository.save(new Notification(testUserId, "test_category", 100L, "Test message100"));
        notificationRepository.save(new Notification(testUserId, "test_category", 101L, "Test message101"));
        entityManager.flush();

//        System.out.println("[TEST] 전체 삭제 요청: userId=" + testUserId);


        mockMvc.perform(delete("/notifications")
                        .param("userId", String.valueOf(testUserId))
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}