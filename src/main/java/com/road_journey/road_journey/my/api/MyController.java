package com.road_journey.road_journey.my.api;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.my.service.MyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
@SecurityRequirement(name = "bearerAuth")
public class MyController {

    private final MyService myService;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUsername();

        Map<String, Object> response = myService.getProfile(userId);

        if ("error".equals(response.get("status"))) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
