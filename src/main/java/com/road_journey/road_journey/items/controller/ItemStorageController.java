package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.items.service.ItemStorageService;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items/storage")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemStorageController {

    private final ItemStorageService itemStorageService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserItems(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "all") String category) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(itemStorageService.getUserItems(userId, category));
    }

    @PatchMapping("/{userItemId}/equip")
    public ResponseEntity<Map<String, Object>> equipItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userItemId,
            @RequestBody Map<String, Boolean> request) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        boolean isEquipped = request.getOrDefault("isEquipped", false);
        return ResponseEntity.ok(itemStorageService.toggleEquipItem(userId, userItemId, isEquipped));
    }
}