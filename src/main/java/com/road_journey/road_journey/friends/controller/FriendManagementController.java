package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.friends.service.FriendManagementService;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public ResponseEntity<Object> getFriendMain(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @PathVariable Long friendId,
                                                @RequestBody(required = false) Map<String, Long> request) {
        Long userId = Long.parseLong(userDetails.getUsername());

        Long notificationId = (request != null && request.containsKey("notificationId"))
                ? request.get("notificationId")
                : null;

        //친구 관계 확인 및 알림 상태 업데이트
        friendManagementService.validateFriendshipAndDeactivateNotification(userId, friendId, notificationId);

        //리다이렉션 응답 반환 // todo 목표 리스트까지 반환해야 함, 메인화면 api를 둘로 분리해놨는데 어캄?
        URI redirectUri = URI.create(String.format("/main/%d", friendId));
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
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