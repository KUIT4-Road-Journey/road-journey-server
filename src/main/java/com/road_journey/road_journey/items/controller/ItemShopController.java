package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.items.service.ItemShopService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items/shop")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemShopController {

    private final ItemShopService itemShopService;

    // 상점 아이템 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getShopItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "all") String category) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(itemShopService.getShopItems(userId, category));
    }

    // 아이템 구매
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long itemId) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(itemShopService.purchaseItem(userId, itemId));
    }
}