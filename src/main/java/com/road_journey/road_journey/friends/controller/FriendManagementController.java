package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.dto.UpdateResponseDTO;
import com.road_journey.road_journey.friends.service.FriendManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendManagementController {

    private final FriendManagementService friendManagementService;

    //친구 목록 조회 (정렬 기준: lastLogin, alphabetical)
    @GetMapping
    public ResponseEntity<List<FriendDTO>> getFriends(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "alphabetical") String category) {
        return ResponseEntity.ok(friendManagementService.getFriends(userId, category));
    }

    //개별 친구 정보 조회
    @GetMapping("/{friendId}/main")
    public ResponseEntity<FriendDTO> getFriendMain(@RequestParam Long userId, @PathVariable Long friendId) {
        return ResponseEntity.ok(friendManagementService.getFriendMain(userId, friendId));
    }

    //친구 좋아요 상태 변경
    @PatchMapping("/{friendId}/likes")
    public ResponseEntity<UpdateResponseDTO> updateFriendLike(
            @RequestParam Long userId,
            @PathVariable Long friendId,
            @RequestBody Map<String, Boolean> request) {
        return ResponseEntity.ok(friendManagementService.updateFriendLike(userId, friendId, request.get("action")));
    }
}