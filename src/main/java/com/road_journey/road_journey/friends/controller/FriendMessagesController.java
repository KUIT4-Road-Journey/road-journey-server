package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.friends.dto.FriendMessageDTO;
import com.road_journey.road_journey.friends.service.FriendMessageService;
import com.road_journey.road_journey.notifications.dto.NotificationDTO;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.road_journey.road_journey.notifications.dto.NotificationCategory.FRIEND;

@RestController
@RequestMapping("/friends/messages")
@RequiredArgsConstructor
public class FriendMessagesController {

    private final NotificationService notificationService;
    private final FriendMessageService friendMessageService;

    //받은 메시지 목록 조회 (Notification 활용)
    @GetMapping
    public ResponseEntity<List<FriendMessageDTO>> getFriendMessages(@RequestParam Long userId) {
        // todo 받은 메세지 생성 로직이 없음 > relatedId 에 goalId 또는 friendId 저장
//        return ResponseEntity.ok(friendMessageService.getFriendMessages(userId)); // todo 주석 해제
        return ResponseEntity.ok(Collections.emptyList());
    }

    //개별 메시지 삭제 (Notification 활용)
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<UpdateResponseDTO> deleteMessage(@RequestParam Long userId, @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }
}