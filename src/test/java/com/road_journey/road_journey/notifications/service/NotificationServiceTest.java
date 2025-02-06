package com.road_journey.road_journey.notifications.service;

import com.road_journey.road_journey.notifications.dto.DeleteResponseDTO;
import com.road_journey.road_journey.notifications.dto.NotificationDTO;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Rollback
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void 알림_목록_조회_테스트() {
        Notification notification = notificationRepository.save(new Notification(1L, "test_category", 100L, "Test message"));


        List<NotificationDTO> notifications = notificationService.getNotifications(1L);


        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).getNotificationId()).isEqualTo(notification.getNotificationId());
        assertThat(notifications.get(0).getMessage()).isEqualTo("Test message");
    }

    @Test
    public void 알림_삭제_테스트() {
        Notification notification = notificationRepository.save(new Notification(1L, "test_category", 100L, "Test message"));


        DeleteResponseDTO response = notificationService.deleteNotification(1L, notification.getNotificationId());


        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    public void 전체_알림_삭제_테스트() {
        notificationRepository.save(new Notification(1L, "notification", 100L, "Test message100"));
        notificationRepository.save(new Notification(1L, "notification", 101L, "Test message101"));


        DeleteResponseDTO response = notificationService.deleteAllNotifications(1L);


        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(notificationRepository.findByUserIdAndStatus(1L, "active")).isEmpty();
    }
}
