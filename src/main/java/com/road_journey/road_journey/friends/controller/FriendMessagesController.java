package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.friends.dto.UpdateResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends/messages")
@RequiredArgsConstructor
public class FriendMessagesController {

    private final NotificationService notificationService;

    //받은 메시지 목록 조회 (Notification 활용)
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getFriendMessages(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByCategory(userId, "친구요청"));
    }

    //개별 메시지 삭제 (Notification 활용)
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<UpdateResponseDTO> deleteMessage(@RequestParam Long userId, @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.deleteNotification(userId, notificationId));
    }
}