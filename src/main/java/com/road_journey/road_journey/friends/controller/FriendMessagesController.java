package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.friends.dto.FriendMessageDTO;
import com.road_journey.road_journey.friends.service.FriendMessageService;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.service.NotificationService;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/friends/messages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendMessagesController {

    private final NotificationService notificationService;
    private final FriendMessageService friendMessageService;
    private final JwtUtil jwtUtil;

    //받은 메시지 목록 조회 (Notification 활용)
    @GetMapping
    public ResponseEntity<List<FriendMessageDTO>> getFriendMessages(@RequestParam Long userId) {
        // todo 받은 메세지 생성 로직이 없음 > relatedId 에 goalId 또는 friendId 저장
//        return ResponseEntity.ok(friendMessageService.getFriendMessages(userId)); // todo 주석 해제
        return ResponseEntity.ok(Collections.emptyList());
    }

    //개별 메시지 삭제 (Notification 활용)
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<UpdateResponseDTO> deleteMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable Long notificationId) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }
}