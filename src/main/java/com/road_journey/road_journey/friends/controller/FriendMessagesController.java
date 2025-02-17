package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.friends.dto.FriendMessageDTO;
import com.road_journey.road_journey.friends.service.FriendMessageService;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends/messages")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendMessagesController {

    private final NotificationService notificationService;
    private final FriendMessageService friendMessageService;

    //받은 메시지 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, List<FriendMessageDTO>>> getFriendMessages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        List<FriendMessageDTO> friendsMessagesDTOs = friendMessageService.getFriendMessages(userId);
        return ResponseEntity.ok(Map.of("messages", friendsMessagesDTOs));
    }

    //개별 메시지 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<UpdateResponseDTO> deleteMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }
}