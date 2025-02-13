package com.road_journey.road_journey.my.api;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.my.domain.AchievementDto;
import com.road_journey.road_journey.my.domain.AchievementResponseDto;
import com.road_journey.road_journey.my.domain.RewardAcceptRequestDto;
import com.road_journey.road_journey.my.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/my/achievement")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<AchievementResponseDto> getAchievements(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "all") String category) {
        String userId = userDetails.getUsername();
        List<AchievementDto> achievements = achievementService.getUserAchievements(Long.parseLong(userId), category);
        return ResponseEntity.ok(new AchievementResponseDto("success", achievements));
    }

    @PostMapping("/reward-accept")
    public ResponseEntity<Map<String, Object>> acceptReward(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RewardAcceptRequestDto request) {
        String userId = userDetails.getUsername();
        boolean isAccepted = achievementService.acceptReward(request.getAchievementId(), Long.parseLong(userId));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("data", Map.of("isRewardAccepted", isAccepted));

        return ResponseEntity.ok(response);
    }
}
