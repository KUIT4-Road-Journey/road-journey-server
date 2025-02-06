package com.road_journey.road_journey.notifications.controller;

import com.road_journey.road_journey.auth.UserDetail;
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
    public ResponseEntity<List<NotificationDTO>> getNotifications(@RequestParam Long userId) { // todo 임시
//        System.out.println("[CONTROLLER] 요청된 사용자 ID: " + userId);
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<DeleteResponseDTO> deleteNotification(@PathVariable Long notificationId, @RequestParam Long userId) { // todo 임시
//        System.out.println("[CONTROLLER] 삭제 요청: userId=" + userId + ", notificationId=" + notificationId);
        return ResponseEntity.ok(notificationService.deleteNotification(userId, notificationId));
    }

    @DeleteMapping
    public ResponseEntity<DeleteResponseDTO> deleteAllNotifications(@RequestParam Long userId) { // todo 임시
//        System.out.println("[CONTROLLER] 전체 삭제 요청: userId=" + userId);
        return ResponseEntity.ok(notificationService.deleteAllNotifications(userId));
    }
}