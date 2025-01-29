package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.items.dto.ItemDto;
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
    public ResponseEntity<?> getSpecialItems() {
        return ResponseEntity.ok(Map.of("availableGold", itemSpecialService.getUserGold(), "specialItems", itemSpecialService.getSpecialItems()));
    }

    @PostMapping("/order")
    public ResponseEntity<?> purchaseSpecialItem(@RequestBody ItemDto itemDto) {
        int remainingGold = itemSpecialService.purchaseSpecialItem(itemDto);
        return ResponseEntity.ok(Map.of("status", "success", "availableGold", remainingGold, "selectedItem", itemDto));
    }
}
