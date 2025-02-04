package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.service.ItemShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items/shop")
public class ItemShopController {

    private final ItemShopService itemShopService;

    public ItemShopController(ItemShopService itemShopService) {
        this.itemShopService = itemShopService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getShopItems(@RequestParam(required = false, defaultValue = "all") String category) {
        return ResponseEntity.ok(itemShopService.getShopItems(category));
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseItem(@RequestParam Long userId, @RequestParam Long itemId) {
        return ResponseEntity.ok(itemShopService.purchaseItem(userId, itemId));
    }
}
