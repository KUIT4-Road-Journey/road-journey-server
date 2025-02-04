package com.road_journey.road_journey.notifications.dto;

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
}
