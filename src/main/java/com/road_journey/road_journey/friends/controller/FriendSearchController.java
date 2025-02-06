package com.road_journey.road_journey.friends.controller;

import com.road_journey.road_journey.auth.UserDTO;
import com.road_journey.road_journey.friends.service.FriendSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/friends/search")
@RequiredArgsConstructor
public class FriendSearchController {

    private final FriendSearchService friendSearchService;

    //친구 검색 (searchId: 사용자 아이디, scope: 검색 범위)
    @GetMapping
    public ResponseEntity<List<UserDTO>> searchFriends(
            @RequestParam Long userId,
            @RequestParam String searchId,
            @RequestParam(required = false, defaultValue = "all") String scope) {
        return ResponseEntity.ok(friendSearchService.searchUsers(userId, searchId, scope));
    }
}