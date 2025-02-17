package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.items.service.ItemSpecialService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/items/special")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemSpecialController {

    private final ItemSpecialService itemSpecialService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSpecialItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(itemSpecialService.getSpecialItems(userId));
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseSpecialItem(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(itemSpecialService.purchaseSpecialItem(userId));
    }
}
