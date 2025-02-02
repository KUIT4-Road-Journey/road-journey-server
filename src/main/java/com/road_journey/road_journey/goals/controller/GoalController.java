package com.road_journey.road_journey.goals.controller;

import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/goals")
@RestController
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping("")
    public String addGoal(@RequestBody AddGoalRequestDto addGoalRequest) {
        goalService.createGoals(addGoalRequest);
        return "Goal Added";
    }

    @GetMapping("/list/{userId}")
    public String getGoalList(@PathVariable Long userId,
                              @RequestParam String category) {
        return "Goal List of " + userId + "\n" +
                "category : " + category;
    }

    @GetMapping("/{goalId}")
    public String getGoalList(@PathVariable Long goalId) {
        return "Goal " + goalId;
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
    public String editGoal(@PathVariable Long goalId) {
        return "Goal " + goalId + " Edited";
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
