package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.UserDetail;
import com.road_journey.road_journey.items.service.ItemShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemShopController {

    private final ItemShopService itemShopService;

    public ItemShopController(ItemShopService itemShopService) {
        this.itemShopService = itemShopService;
    }

    @GetMapping("/shop")
    public ResponseEntity<Map<String, Object>> getShopItems(UserDetail userDetail,
                                                            @RequestParam(required = false, defaultValue = "all") String category) {
        Map<String, Object> response = Map.of(
                "availableGold", itemShopService.getUserGold(userDetail.getUserId()),
                "shopItems", itemShopService.getShopItems(userDetail.getUserId(), category));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseItem(UserDetail userDetail,
                                                            @RequestBody Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("아이템 ID가 필요합니다.");
        }
        Map<String, Object> response = itemShopService.purchaseItem(userDetail.getUserId(), itemId);

        return ResponseEntity.ok(response);
    }
}
