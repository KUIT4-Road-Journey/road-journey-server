package com.road_journey.road_journey.items.controller;

import com.road_journey.road_journey.auth.UserDetail;
import com.road_journey.road_journey.items.service.ItemSpecialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/items/special")
public class ItemSpecialController {

    private final ItemSpecialService itemSpecialService;

    public ItemSpecialController(ItemSpecialService itemSpecialService) {
        this.itemSpecialService = itemSpecialService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSpecialItems(UserDetail userDetail) {
        return ResponseEntity.ok(itemSpecialService.getSpecialItems(userDetail.getUserId()));
    }

    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> purchaseSpecialItem(UserDetail userDetail) {
        return ResponseEntity.ok(itemSpecialService.purchaseSpecialItem(userDetail.getUserId()));
    }
}
