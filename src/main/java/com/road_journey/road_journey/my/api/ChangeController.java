package com.road_journey.road_journey.my.api;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.my.domain.*;
import com.road_journey.road_journey.my.service.ChangeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my/change")
@SecurityRequirement(name = "bearerAuth")
public class ChangeController {

    private final ChangeService changeService;

    @PatchMapping("/id")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> changeMyId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangeAccountIdRequestDto request) {
        changeService.changeUserId(userDetails.getUsername(), request.getAccountId());
        return ResponseEntity.ok(Map.of("status", "success", "message", "Id updated successfully"));
    }

    @PatchMapping("/password")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> changeMyPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangeAccountPwRequestDto request) {
        changeService.changeUserPassword(userDetails.getUsername(), request.getAccountPassword());
        return ResponseEntity.ok(Map.of("status", "success", "message", "Password updated successfully"));
    }

    @PatchMapping("/nickname")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> changeMyNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangeNicknameRequestDto request) {
        changeService.changeUserNickname(userDetails.getUsername(), request.getNickname());
        return ResponseEntity.ok(Map.of("status", "success", "message", "Nickname updated successfully"));
    }

    @PatchMapping("/email")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> changeMyEmail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangeEmailRequestDto request) {
        changeService.changeUserEmail(userDetails.getUsername(), request.getEmail());
        return ResponseEntity.ok(Map.of("status", "success", "message", "Email updated successfully"));
    }

    @PatchMapping("/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> changeMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangeProfileRequestDto request) {
        changeService.changeUserProfile(userDetails.getUsername(), request.getProfileImage(), request.getStatusMessage());
        return ResponseEntity.ok(Map.of("status", "success", "message", "Profile updated successfully"));
    }

}
