package com.road_journey.road_journey.main.controller;

import com.road_journey.road_journey.goals.response.ResponseStatus;
import com.road_journey.road_journey.main.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/main")
@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @GetMapping("/{userId}")
    public ResponseStatus getMain(@PathVariable Long userId) {
        return mainService.getMain(userId);
    }
}