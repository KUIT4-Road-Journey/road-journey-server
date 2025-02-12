package com.road_journey.road_journey.goals.controller;

import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.dto.GoalListResponseDto;
import com.road_journey.road_journey.goals.dto.GoalResponseDto;
import com.road_journey.road_journey.goals.response.BaseResponse;
import com.road_journey.road_journey.goals.response.ResponseStatus;
import com.road_journey.road_journey.goals.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/goals")
@RestController
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping("")
    public ResponseStatus addGoal(@RequestBody AddGoalRequestDto addGoalRequest) {
        return goalService.createGoals(addGoalRequest);
        // TODO 요청 실패 시 처리
    }

    @GetMapping("/list/{userId}")
    public ResponseStatus getGoalList(@PathVariable Long userId,
                                      @RequestParam String category) {
        return goalService.getGoalListResponse(userId, category);
    }

    @GetMapping("/{goalId}")
    public ResponseStatus getGoalList(@PathVariable Long goalId) {
        return new BaseResponse<>(goalService.getGoalResponseByGoalId(goalId)); // TODO null 반환할 경우 처리
    }

    @PostMapping("/{goalId}/accept")
    public ResponseStatus acceptGoal(@PathVariable Long goalId) {
        return goalService.processPendingGoal(goalId, true, true);
    }

    @PostMapping("/{goalId}/reject")
    public ResponseStatus rejectGoal(@PathVariable Long goalId) {
        return goalService.processPendingGoal(goalId, true, false);
    }

    @PostMapping("/{goalId}/edit")
    public ResponseStatus editGoal(@PathVariable Long goalId,
                           @RequestBody AddGoalRequestDto addGoalRequest) {

        return goalService.editGoals(goalId, addGoalRequest);
    }

    @PostMapping("/{goalId}/edit/accept")
    public ResponseStatus acceptGoalEdition(@PathVariable Long goalId) {
        return goalService.processPendingGoal(goalId, false, true);
    }

    @PostMapping("/{goalId}/edit/reject")
    public ResponseStatus rejectGoalEdition(@PathVariable Long goalId) {
        return goalService.processPendingGoal(goalId, false, false);
    }

    @PostMapping("/{goalId}/complete")
    public ResponseStatus completeGoal(@PathVariable Long goalId) {
        return goalService.completeGoal(goalId);
    }

    @PostMapping("/{goalId}/fail")
    public ResponseStatus failGoal(@PathVariable Long goalId) {
        return goalService.failGoal(goalId);
    }

    @PostMapping("/{goalId}/reward")
    public ResponseStatus getReward(@PathVariable Long goalId) {
        return goalService.getRewardByGoalId(goalId);
    }

    @PostMapping("/{goalId}/sub-goals/{subGoalId}/complete")
    public ResponseStatus completeSubGoal(@PathVariable Long goalId,
                                  @PathVariable Long subGoalId) {
        return goalService.completeSubGoal(goalId, subGoalId);
    }
}
