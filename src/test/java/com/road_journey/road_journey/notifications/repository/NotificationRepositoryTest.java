package com.road_journey.road_journey.notifications.repository;

import com.road_journey.road_journey.notifications.entity.Notification;
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
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void 알림_저장_조회_테스트() {
        notificationRepository.save(new Notification(1L, "test_category", 100L, "Test message"));


        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(1L, "active");


        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).getMessage()).isEqualTo("Test message");
    }

    @Test
    public void 알림_삭제_테스트() {
        Notification notification = notificationRepository.save(new Notification(1L, "test_category", 100L, "Test message"));


        notificationRepository.updateStatusByUserIdAndId(1L, notification.getNotificationId(), "deleted");


        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(1L, "active");
        assertThat(notifications).isEmpty();
    }
}