package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.friends.dto.FriendUserDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.service.FriendRequestService;
import com.road_journey.road_journey.notifications.dto.NotificationCategory;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends/requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    //친구 요청 보내기
    @PostMapping
    public ResponseEntity<UpdateResponseDTO> sendFriendRequest(
            @RequestParam Long userId,
            @RequestBody Map<String, Long> request) {
        Long friendUserId = request.get("friendUserId");

        // 친구 요청을 대기 상태로 저장
        friendRequestService.sendFriendRequest(userId, friendUserId);

        return ResponseEntity.ok(new UpdateResponseDTO("success", "Friend request sent successfully."));
    }

    //친구 요청 목록 조회 (`status = 'pending'`)
    @GetMapping
    public ResponseEntity<List<FriendUserDTO>> getFriendRequests(@RequestParam Long userId) {
        return ResponseEntity.ok(friendRequestService.getFriendRequests(userId));
    }

    //친구 요청 수락/거절
    @PostMapping("/{friendId}/action")
    public ResponseEntity<UpdateResponseDTO> handleFriendRequest(
            @RequestParam Long userId,
            @PathVariable Long friendId,
            @RequestBody Map<String, String> request) {

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
