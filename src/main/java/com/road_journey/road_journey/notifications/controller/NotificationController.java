package com.road_journey.road_journey.notifications.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.notifications.dto.NotificationDTO;
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
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Map<String, List<NotificationDTO>>> getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        List<NotificationDTO> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(Map.of("notifications", notifications));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<UpdateResponseDTO> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }

    @DeleteMapping
    public ResponseEntity<UpdateResponseDTO> deleteAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(notificationService.deleteAllNotifications(userId));
    }
}