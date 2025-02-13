package com.road_journey.road_journey.goals.controller;

import com.road_journey.road_journey.auth.domain.CustomUserDetails;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.response.BaseResponse;
import com.road_journey.road_journey.goals.response.ResponseStatus;
import com.road_journey.road_journey.goals.service.GoalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/goals")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ROLE_USER')")
@RestController
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping("")
    public ResponseStatus addGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestBody AddGoalRequestDto addGoalRequest) {
        return goalService.createGoals(Long.valueOf(userDetails.getUsername()), addGoalRequest);
        // TODO 요청 실패 시 처리
    }

    @GetMapping("/list/{userId}")
    public ResponseStatus getGoalList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @PathVariable Long userId,
                                      @RequestParam String category) {
        return goalService.getGoalListResponse(Long.valueOf(userDetails.getUsername()), userId, category);
    }

    @GetMapping("/{goalId}")
    public ResponseStatus getGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long goalId) {
        return new BaseResponse<>(goalService.getGoalResponseByGoalId(Long.valueOf(userDetails.getUsername()), goalId));
    }

    @PostMapping("/{goalId}/accept")
    public ResponseStatus acceptGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @PathVariable Long goalId) {
        return goalService.processPendingGoal(Long.valueOf(userDetails.getUsername()), goalId, true, true);
    }

    @PostMapping("/{goalId}/reject")
    public ResponseStatus rejectGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @PathVariable Long goalId) {
        return goalService.processPendingGoal(Long.valueOf(userDetails.getUsername()), goalId, true, false);
    }

    @PostMapping("/{goalId}/edit")
    public ResponseStatus editGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @PathVariable Long goalId,
                                   @RequestBody AddGoalRequestDto addGoalRequest) {

        return goalService.editGoals(Long.valueOf(userDetails.getUsername()), goalId, addGoalRequest);
    }

    @PostMapping("/{goalId}/edit/accept")
    public ResponseStatus acceptGoalEdition(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long goalId) {
        return goalService.processPendingGoal(Long.valueOf(userDetails.getUsername()), goalId, false, true);
    }

    @PostMapping("/{goalId}/edit/reject")
    public ResponseStatus rejectGoalEdition(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long goalId) {
        return goalService.processPendingGoal(Long.valueOf(userDetails.getUsername()), goalId, false, false);
    }

    @PostMapping("/{goalId}/complete")
    public ResponseStatus completeGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @PathVariable Long goalId) {
        return goalService.completeGoal(Long.valueOf(userDetails.getUsername()), goalId);
    }

    @PostMapping("/{goalId}/fail")
    public ResponseStatus failGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @PathVariable Long goalId) {
        return goalService.failGoal(Long.valueOf(userDetails.getUsername()), goalId);
    }

    @PostMapping("/{goalId}/reward")
    public ResponseStatus getReward(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable Long goalId) {
        return goalService.getRewardByGoalId(Long.valueOf(userDetails.getUsername()), goalId);
    }

    @PostMapping("/{goalId}/sub-goals/{subGoalId}/complete")
    public ResponseStatus completeSubGoal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @PathVariable Long goalId,
                                          @PathVariable Long subGoalId) {
        return goalService.completeSubGoal(Long.valueOf(userDetails.getUsername()), goalId, subGoalId);
    }
}
