package com.road_journey.road_journey.notifications.service;

import com.road_journey.road_journey.notifications.dto.DeleteResponseDTO;
import com.road_journey.road_journey.notifications.dto.NotificationDTO;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDTO> getNotifications(Long userId) {
        return notificationRepository.findByUserIdAndStatus(userId, "active").stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeleteResponseDTO deleteNotification(Long userId, Long notificationId) {
        Optional<Notification> notification = notificationRepository.findByUserIdAndNotificationId(userId, notificationId);

        if (notification.isPresent()) {
            Notification notif = notification.get();

            if (!"deleted".equals(notif.getStatus())) {
                notificationRepository.updateStatusByUserIdAndId(userId, notificationId, "deleted");
                return new DeleteResponseDTO("success", "Notification deleted.");
            } else {
                return new DeleteResponseDTO("error", "Notification is already deleted.");
            }
        } else {
            return new DeleteResponseDTO("error", "Notification not found or does not belong to the user.");
        }
    }

    @Transactional
    public DeleteResponseDTO deleteAllNotifications(Long userId) {
        List<Notification> activeNotifications = notificationRepository.findByUserIdAndStatus(userId, "active");

        if (activeNotifications.isEmpty()) {
            return new DeleteResponseDTO("error", "No active notifications found to delete.");
        }

        notificationRepository.updateStatusByUserIdAndCategory(userId, "notification", "deleted");
        return new DeleteResponseDTO("success", "All notifications deleted.");
    }
}
