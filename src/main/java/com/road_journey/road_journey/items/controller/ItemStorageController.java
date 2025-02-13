package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.items.service.ItemStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items/storage")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemStorageController {

    private final ItemStorageService itemStorageService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "all") String category) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(itemStorageService.getUserItems(userId, category));
    }

    @PatchMapping("/{userItemId}/equip")
    public ResponseEntity<Map<String, Object>> equipItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userItemId,
            @RequestBody Map<String, Boolean> request) {
        Long userId = Long.parseLong(userDetails.getUsername());

        boolean isEquipped = request.getOrDefault("isEquipped", false);
        return ResponseEntity.ok(itemStorageService.toggleEquipItem(userId, userItemId, isEquipped));
    }

    @GetMapping("/equipped")
    public ResponseEntity<Map<String, Object>> getEquippedItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long friendId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userId = (friendId != null) ? friendId : userId;

        return ResponseEntity.ok(itemStorageService.getEquippedItems(userId));
    }
}