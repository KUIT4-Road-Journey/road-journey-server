package com.road_journey.road_journey.main.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.goals.response.ResponseStatus;
import com.road_journey.road_journey.main.service.MainService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/main")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @GetMapping("/{userId}")
    public ResponseStatus getMain(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long userId) {
        return mainService.getMain(Long.valueOf(userDetails.getUsername()), userId);
    }
}