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
        return new BaseResponse<>(goalService.getGoalListResponse(userId, category));
    }

    @GetMapping("/{goalId}")
    public ResponseStatus getGoalList(@PathVariable Long goalId) {
        return new BaseResponse<>(goalService.getGoalResponseByGoalId(goalId)); // TODO null 반환할 경우 처리
    }

    @PostMapping("/{goalId}/accept")
    public String acceptGoal(@PathVariable Long goalId) {

        return "Goal " + goalId + " Accepted";
    }

    @PostMapping("/{goalId}/reject")
    public String rejectGoal(@PathVariable Long goalId) {
        return "Goal " + goalId + " Rejected";
    }

    @PostMapping("/{goalId}/edit")
    public ResponseStatus editGoal(@PathVariable Long goalId,
                           @RequestBody AddGoalRequestDto addGoalRequest) {

        return goalService.editGoals(goalId, addGoalRequest);
    }

    @PostMapping("/{goalId}/edit/accept")
    public String acceptGoalEdition(@PathVariable Long goalId) {
        return "Goal " + goalId + " Edition Accepted";
    }

    @PostMapping("/{goalId}/edit/reject")
    public String rejectGoalEdition(@PathVariable Long goalId) {
        return "Goal " + goalId + " Edition Rejected";
    }

    @PostMapping("/{goalId}/complete")
    public String completeGoal(@PathVariable Long goalId) {
        return "Goal " + goalId + " Completed";
    }

    @PostMapping("/{goalId}/fail")
    public String failGoal(@PathVariable Long goalId) {
        return "Goal " + goalId + " Failed";
    }

    @PostMapping("/{goalId}/sub-goals/{subGoalId}/complete")
    public String completeSubGoal(@PathVariable Long goalId,
                                  @PathVariable Long subGoalId) {
        return "Sub-Goal " + subGoalId + " of Goal " + goalId + " Completed";
    }

    @PostMapping("/{goalId}/sub-goals/{subGoalId}/fail")
    public String failSubGoal(@PathVariable Long goalId,
                              @PathVariable Long subGoalId) {
        return "Sub-Goal " + subGoalId + " of Goal " + goalId + " Failed";
    }
}
