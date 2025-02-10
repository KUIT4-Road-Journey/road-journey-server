package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.service.ItemShopService;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items/shop")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemShopController {

    private final ItemShopService itemShopService;
    private final JwtUtil jwtUtil;

    // 상점 아이템 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getShopItems(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false, defaultValue = "all") String category) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(itemShopService.getShopItems(userId, category));
    }

    // 아이템 구매
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseItem(
            @RequestHeader("Authorization") String token,
            @RequestParam Long itemId) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(itemShopService.purchaseItem(userId, itemId));
    }
}