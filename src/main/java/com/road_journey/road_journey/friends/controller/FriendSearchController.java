package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.service.FriendSearchService;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends/search")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class FriendSearchController {

    private final FriendSearchService friendSearchService;
    private final JwtUtil jwtUtil;

    // 친구 검색 (searchId: 사용자 아이디)
    @GetMapping
    public ResponseEntity<Map<String, List<FriendDTO>>> searchFriends(
            @RequestHeader("Authorization") String token,
            @RequestParam String searchId) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        List<FriendDTO> friendDTOs = friendSearchService.searchUsers(userId, searchId);
        return ResponseEntity.ok(Map.of("users", friendDTOs));
    }
}