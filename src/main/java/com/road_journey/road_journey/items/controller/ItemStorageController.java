package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.items.service.ItemStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/items/storage")
public class ItemStorageController {

    private final ItemStorageService itemStorageService;

    public ItemStorageController(ItemStorageService itemStorageService) {
        this.itemStorageService = itemStorageService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserItems(@RequestParam Long userId, //todo userId 수정
                                                            @RequestParam(defaultValue = "all") String category) {
        return ResponseEntity.ok(itemStorageService.getUserItems(userId, category));
    }

    @PatchMapping("/{userItemId}/equip")
    public ResponseEntity<Map<String, Object>> equipItem(@RequestParam Long userId, //todo userId 수정
                                                         @PathVariable Long userItemId,
                                                         @RequestBody Map<String, Boolean> request) {
        boolean isEquipped = request.getOrDefault("isEquipped", false);

        return ResponseEntity.ok(itemStorageService.toggleEquipItem(userId, userItemId, isEquipped));
    }
}