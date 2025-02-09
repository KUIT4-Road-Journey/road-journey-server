package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.service.FriendRequestService;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends/requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final JwtUtil jwtUtil;

    // 친구 요청 목록 조회 (`status = 'pending'`)
    @GetMapping
    public ResponseEntity<List<FriendDTO>> getFriendRequests(@RequestHeader("Authorization") String token) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);

        Long userId = jwtUtil.getUserId(token);
        return ResponseEntity.ok(friendRequestService.getFriendRequests(userId));
    }

    // 친구 요청 보내기
    @PostMapping
    public ResponseEntity<UpdateResponseDTO> sendFriendRequest(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Long> request) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);

        Long userId = jwtUtil.getUserId(token);
        Long friendUserId = request.get("friendUserId");

        friendRequestService.sendFriendRequest(userId, friendUserId);

        return ResponseEntity.ok(new UpdateResponseDTO("success", "Friend request sent successfully."));
    }

    // 친구 요청 수락/거절
    @PostMapping("/{friendId}/action")
    public ResponseEntity<UpdateResponseDTO> handleFriendRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable Long friendId,
            @RequestBody Map<String, String> request) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);

        Long userId = jwtUtil.getUserId(token);
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