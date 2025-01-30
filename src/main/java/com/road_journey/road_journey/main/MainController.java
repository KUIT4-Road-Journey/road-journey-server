package com.road_journey.road_journey.main;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/main")
@RestController
public class MainController {

    @GetMapping("/{userId}")
    public String getMain(@PathVariable Long userId) {
        return "Main of " + userId;
    }
}