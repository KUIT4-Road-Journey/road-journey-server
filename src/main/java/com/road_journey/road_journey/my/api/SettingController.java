package com.road_journey.road_journey.my.api;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.my.domain.SettingDto;
import com.road_journey.road_journey.my.domain.SettingResponseDto;
import com.road_journey.road_journey.my.domain.ToggleRequestDto;
import com.road_journey.road_journey.my.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/my/setting")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping("")
    public ResponseEntity<SettingResponseDto> getUserSettings(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUsername();
        List<SettingDto> settings = settingService.getUserSettings(Long.parseLong(userId));
        return ResponseEntity.ok(new SettingResponseDto("success", settings));
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleSetting(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ToggleRequestDto request) {
        String userId = userDetails.getUsername();
        boolean newStatus = settingService.toggleUserSetting(request.getSettingId(), Long.parseLong(userId));
        return ResponseEntity.ok(Map.of("status", "success", "data", Map.of("isActivated", newStatus)));
    }
}
