package com.road_journey.road_journey.notifications.repository;

import com.road_journey.road_journey.notifications.entity.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.road_journey.road_journey.notifications.dto.NotificationCategory.FRIEND;
import static com.road_journey.road_journey.notifications.dto.NotificationCategory.NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Rollback
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void 알림_저장_조회_테스트() {
        notificationRepository.save(new Notification(1L, NOTIFICATION.name(), 100L, "Test message"));


        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(1L, "active");


        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).getMessage()).isEqualTo("Test message");
    }

    @Test
    public void 알림_삭제_테스트() {
        Notification notification = notificationRepository.save(new Notification(1L, NOTIFICATION.name(), 100L, "Test message"));


        notificationRepository.updateStatusByNotificationId(notification.getNotificationId(), "deleted");


        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(1L, "active");
        assertThat(notifications).isEmpty();
    }

    @Test
    public void 카테고리별_알림_검색_테스트() {
        notificationRepository.save(new Notification(1L, NOTIFICATION.name(), 100L, "Test message 1"));
        notificationRepository.save(new Notification(1L, FRIEND.name(), 101L, "Test message 2"));
        notificationRepository.save(new Notification(1L, NOTIFICATION.name(), 102L, "Test message 3"));


        List<Notification> activeNotifications = notificationRepository.findActiveNotificationsByCategory(1L, NOTIFICATION.name());


        assertThat(activeNotifications).isNotEmpty();
        assertThat(activeNotifications).hasSize(2);
        assertThat(activeNotifications.get(0).getRelatedId()).isEqualTo(100L);
        assertThat(activeNotifications.get(1).getRelatedId()).isEqualTo(102L);
    }

    @Test
    public void 카테고리별_알림_상태_업데이트_테스트() {
        Notification notification1 = notificationRepository.save(new Notification(1L, NOTIFICATION.name(), 100L, "Test message1"));
        Notification notification2 = notificationRepository.save(new Notification(1L, NOTIFICATION.name(), 101L, "Test message2"));

        System.out.println("Created Notification ID: " + notification1.getNotificationId());
        System.out.println("Created Notification ID: " + notification2.getNotificationId());
        notificationRepository.updateStatusByNotificationIdAndCategory(notification2.getNotificationId(), NOTIFICATION.name(), "deleted");

        entityManager.clear();

        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(1L, "deleted");
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).getRelatedId()).isEqualTo(101L);
        assertThat(notifications.get(0).getStatus()).isEqualTo("deleted");
    }
}