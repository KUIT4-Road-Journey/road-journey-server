package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.service.FriendManagementService;
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
@RequestMapping("/friends")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendManagementController {

    private final FriendManagementService friendManagementService;

    // 친구 목록 조회 (정렬 기준: lastLogin, alphabetical)
    @GetMapping
    public ResponseEntity<Map<String, List<FriendListDTO>>> getFriends(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "alphabetical") String sortBy) {
        Long userId = Long.parseLong(userDetails.getUsername());

        List<FriendListDTO> FriendListDTOs = friendManagementService.getFriends(userId, sortBy);
        return ResponseEntity.ok(Map.of("friends", FriendListDTOs));
    }

    @GetMapping("/{friendId}/main")
    public ResponseEntity<Object> getFriendMain(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(friendManagementService.getFriendMain());
    }

    //친구 좋아요 상태 변경
    @PatchMapping("/{friendId}/likes")
    public ResponseEntity<UpdateResponseDTO> updateFriendLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long friendId,
            @RequestBody Map<String, Boolean> request) {
        Long userId = Long.parseLong(userDetails.getUsername());

        friendManagementService.updateFriendLike(friendId, request.get("action"));
        return ResponseEntity.ok(new UpdateResponseDTO("success", "Friend like status updated."));
    }
}