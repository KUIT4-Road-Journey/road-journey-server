package com.road_journey.road_journey.notifications.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.notifications.dto.NotificationDTO;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.service.NotificationService;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<Map<String, List<NotificationDTO>>> getNotifications(@RequestHeader("Authorization") String token) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        List<NotificationDTO> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(Map.of("notifications", notifications));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<UpdateResponseDTO> deleteNotification(
            @RequestHeader("Authorization") String token,
            @PathVariable Long notificationId) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }

    @DeleteMapping
    public ResponseEntity<UpdateResponseDTO> deleteAllNotifications(@RequestHeader("Authorization") String token) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);
        return ResponseEntity.ok(notificationService.deleteAllNotifications(userId));
    }
}