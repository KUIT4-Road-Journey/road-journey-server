package com.road_journey.road_journey.notifications.repository;

import com.road_journey.road_journey.notifications.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdAndStatus(Long userId, String status);

    Optional<Notification> findByUserIdAndNotificationId(Long userId, Long notificationId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.status = :status WHERE n.userId = :userId AND n.notificationId = :notificationId")
    void updateStatusByUserIdAndId(Long userId, Long notificationId, String status);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.status = :status WHERE n.userId = :userId AND n.category = :category")
    void updateStatusByUserIdAndCategory(Long userId, String category, String status);

    //todo test 필요
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.category = :category AND n.status = 'active'")
    List<Notification> findActiveNotificationsByCategory(Long userId, String category);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.status = :status WHERE n.relatedId = :relatedId AND n.category = :category")
    void updateStatusByRelatedIdAndCategory(Long relatedId, String category, String status);
}

