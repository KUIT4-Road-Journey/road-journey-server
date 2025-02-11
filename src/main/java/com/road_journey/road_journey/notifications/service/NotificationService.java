package com.road_journey.road_journey.notifications.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.notifications.dto.NotificationCategory;
import com.road_journey.road_journey.notifications.dto.NotificationDTO;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.road_journey.road_journey.notifications.dto.NotificationCategory.NOTIFICATION;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationDTO> getNotifications(Long userId) {
        return notificationRepository.findByUserIdAndCategoryAndStatus(userId, "NOTIFICATION", "active").stream()
                .map(notification -> {
                    String imageUrl = null;
                    if (notification.getRelatedId() != null) {
                        imageUrl = userRepository.findProfileImageByUserId(notification.getRelatedId());
                    }
                    return new NotificationDTO(notification, imageUrl);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public UpdateResponseDTO deleteNotification(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findByNotificationId(notificationId);

        if (notification.isPresent()) {
            Notification notif = notification.get();

            if (!"deleted".equals(notif.getStatus())) {
                notificationRepository.updateStatusByNotificationId(notificationId, "deleted");
                return new UpdateResponseDTO("success", "Notification deleted.");
            } else {
                return new UpdateResponseDTO("error", "Notification is already deleted.");
            }
        } else {
            return new UpdateResponseDTO("error", "Notification not found or does not belong to the user.");
        }
    }

    @Transactional
    public UpdateResponseDTO deleteAllNotifications(Long userId) {
        List<Notification> activeNotifications = notificationRepository.findByUserIdAndStatus(userId, "active");

        if (activeNotifications.isEmpty()) {
            return new UpdateResponseDTO("error", "No active notifications found to delete.");
        }

        notificationRepository.updateStatusByUserIdAndCategory(userId, NOTIFICATION.name(), "deleted");
        return new UpdateResponseDTO("success", "All notifications deleted.");
    }

    public List<NotificationDTO> getNotificationsByCategory(Long userId, NotificationCategory category) {
        return notificationRepository.findActiveNotificationsByCategory(userId, category.name()).stream()
                .map(notification -> {
                    String imageUrl = null;
                    if (notification.getRelatedId() != null) {
                        imageUrl = userRepository.findProfileImageByUserId(notification.getRelatedId());
                    }
                    return new NotificationDTO(notification, imageUrl);
                })
                .collect(Collectors.toList());
    }

//    @Transactional
//    public void createNotification(Long userId, String category, Long relatedId) {
//        Notification notification = new Notification();
//        notification.setUserId(userId);
//        notification.setCategory(category);
//        notification.setRelatedId(relatedId);
//        notification.setMessage("You have a new friend request.");
//        notification.setStatus("active");
//        notificationRepository.save(notification);
//    }
}
