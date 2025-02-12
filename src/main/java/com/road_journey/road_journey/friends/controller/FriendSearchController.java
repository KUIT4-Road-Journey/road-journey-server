package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.service.FriendSearchService;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends/search")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendSearchController {

    private final FriendSearchService friendSearchService;
    private final JwtUtil jwtUtil;

    // 친구 검색 (searchId: 사용자 아이디)
    @GetMapping
    public ResponseEntity<Map<String, List<FriendDTO>>> searchFriends(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String searchId) {
        Long userId = Long.parseLong(userDetails.getUsername());

        List<FriendDTO> friendDTOs = friendSearchService.searchUsers(userId, searchId);
        return ResponseEntity.ok(Map.of("users", friendDTOs));
    }
}