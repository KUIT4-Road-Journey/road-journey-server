package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.config.JwtUtil;
import com.road_journey.road_journey.items.service.ItemSpecialService;
import com.road_journey.road_journey.utils.TokenValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items/special")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemSpecialController {

    private final ItemSpecialService itemSpecialService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSpecialItems(@RequestHeader("Authorization") String token) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(itemSpecialService.getSpecialItems(userId));
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseSpecialItem(@RequestHeader("Authorization") String token) {
        token = TokenValidatorUtil.validateToken(token, jwtUtil);
        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(itemSpecialService.purchaseSpecialItem(userId));
    }
}
