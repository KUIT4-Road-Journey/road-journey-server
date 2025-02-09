package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.service.FriendManagementService;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendManagementController {

    private final FriendManagementService friendManagementService;
    private final JwtUtil jwtUtil;

    // 친구 목록 조회 (정렬 기준: lastLogin, alphabetical)
    @GetMapping
    public ResponseEntity<List<FriendListDTO>> getFriends(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false, defaultValue = "alphabetical") String sortBy) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(friendManagementService.getFriends(userId, sortBy));
    }

    @GetMapping("/{friendId}/main")
    public ResponseEntity<Object> getFriendMain(@PathVariable Long friendId) {
        return ResponseEntity.ok(friendManagementService.getFriendMain());
    }

    //친구 좋아요 상태 변경
    @PatchMapping("/{friendId}/likes")
    public ResponseEntity<UpdateResponseDTO> updateFriendLike(
            @RequestHeader("Authorization") String token,
            @PathVariable Long friendId,
            @RequestBody Map<String, Boolean> request) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        friendManagementService.updateFriendLike(friendId, request.get("action"));
        return ResponseEntity.ok(new UpdateResponseDTO("success", "Friend like status updated."));
    }
}