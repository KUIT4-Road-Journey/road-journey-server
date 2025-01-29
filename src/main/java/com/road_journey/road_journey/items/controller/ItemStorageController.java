package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.items.dto.ItemDto;
import com.road_journey.road_journey.items.service.ItemStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items/storage")
public class ItemStorageController {

    private final ItemStorageService itemStorageService;

    public ItemStorageController(ItemStorageService itemStorageService) {
        this.itemStorageService = itemStorageService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestParam(required = false, defaultValue = "all") String category) {
        return ResponseEntity.ok(itemStorageService.getUserItems(category));
    }

    @PatchMapping("/{userItemId}/equip")
    public ResponseEntity<?> equipItem(@PathVariable Long userItemId, @RequestBody Map<String, Boolean> request) {
        boolean isEquipped = request.getOrDefault("isEquipped", false);
        itemStorageService.equipItem(userItemId, isEquipped);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Item equipped successfully."));
    }
}
