package com.road_journey.road_journey.notifications.dto;

import com.road_journey.road_journey.notifications.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private String message;
    private String createdAt;
    private String category;
    private Long relatedId;
    private String status;

    public NotificationDTO(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.message = notification.getMessage();
        this.createdAt = notification.getCreatedAt().toString();
        this.category = notification.getCategory();
        this.relatedId = notification.getRelatedId();
        this.status = notification.getStatus();
    }
}
