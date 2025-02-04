package com.road_journey.road_journey.notifications.controller;

import com.road_journey.road_journey.notifications.dto.DeleteResponseDTO;
import com.road_journey.road_journey.notifications.dto.NotificationDTO;
import com.road_journey.road_journey.notifications.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(UserDetail userDetail) {
        return ResponseEntity.ok(notificationService.getNotifications(userDetail.getUserId()));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<DeleteResponseDTO> deleteNotification(@PathVariable Long notificationId, UserDetail userDetail) {
        return ResponseEntity.ok(notificationService.deleteNotification(userDetail.getUserId(), notificationId));
    }

    @DeleteMapping
    public ResponseEntity<DeleteResponseDTO> deleteAllNotifications(UserDetail userDetail) {
        return ResponseEntity.ok(notificationService.deleteAllNotifications(userDetail.getUserId()));
    }
}