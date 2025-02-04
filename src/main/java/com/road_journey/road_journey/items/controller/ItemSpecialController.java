package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.items.service.ItemSpecialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items/special")
public class ItemSpecialController {

    private final ItemSpecialService itemSpecialService;

    public ItemSpecialController(ItemSpecialService itemSpecialService) {
        this.itemSpecialService = itemSpecialService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSpecialItems(@RequestParam Long userId) {
        return ResponseEntity.ok(itemSpecialService.getSpecialItems(userId));
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseSpecialItem(@RequestParam Long userId) {
        return ResponseEntity.ok(itemSpecialService.purchaseSpecialItem(userId));
    }
}
