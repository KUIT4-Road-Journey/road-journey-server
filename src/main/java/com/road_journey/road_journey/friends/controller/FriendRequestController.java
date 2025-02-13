package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.service.FriendRequestService;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends/requests")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    // 친구 요청 목록 조회 (`status = 'pending'`)
    @GetMapping
    public ResponseEntity<Map<String, List<FriendDTO>>> getFriendRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        List<FriendDTO> friendDTOs = friendRequestService.getFriendRequests(userId);
        return ResponseEntity.ok(Map.of("requests", friendDTOs));
    }

    // 친구 요청 보내기
    @PostMapping
    public ResponseEntity<UpdateResponseDTO> sendFriendRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Long> request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Long friendUserId = request.get("friendUserId");

        friendRequestService.sendFriendRequest(userId, friendUserId);
        return ResponseEntity.ok(new UpdateResponseDTO("success", "Friend request sent successfully."));
    }

    // 친구 요청 수락/거절
    @PostMapping("/{friendId}/action")
    public ResponseEntity<UpdateResponseDTO> handleFriendRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long friendId,
            @RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String action = request.get("action");

        if ("accept".equalsIgnoreCase(action)) {
            friendRequestService.acceptFriendRequest(userId, friendId);
            return ResponseEntity.ok(new UpdateResponseDTO("success", "Friend request accepted."));
        } else if ("reject".equalsIgnoreCase(action)) {
            friendRequestService.rejectFriendRequest(userId, friendId);
            return ResponseEntity.ok(new UpdateResponseDTO("success", "Friend request rejected."));
        } else {
            return ResponseEntity.badRequest().body(new UpdateResponseDTO("error", "Invalid action."));
        }
    }
}