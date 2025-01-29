package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.service.ItemShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items/shop")
public class ItemShopController {

    private final ItemShopService itemShopService;

    public ItemShopController(ItemShopService itemShopService) {
        this.itemShopService = itemShopService;
    }

    @GetMapping
    public ResponseEntity<?> getShopItems(@RequestParam(required = false, defaultValue = "all") String category) {
        return ResponseEntity.ok(Map.of("availableGold", itemShopService.getUserGold(), "shopItems", itemShopService.getShopItems(category)));
    }

    @PostMapping("/order")
    public ResponseEntity<?> purchaseItem(@RequestBody ItemDto itemDto) {
        int remainingGold = itemShopService.purchaseItem(itemDto);
        return ResponseEntity.ok(Map.of("status", "success", "availableGold", remainingGold));
    }
}
