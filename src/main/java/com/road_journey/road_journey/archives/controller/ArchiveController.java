package com.road_journey.road_journey.archives.controller;

import com.road_journey.road_journey.goals.dto.GoalResponseDto;
import com.road_journey.road_journey.goals.response.BaseResponse;
import com.road_journey.road_journey.goals.response.ResponseStatus;
import com.road_journey.road_journey.goals.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/archives")
@RestController
public class ArchiveController {

    @Autowired
    private GoalService goalService;

    @GetMapping("/list/{userId}")
    public ResponseStatus getArchiveList(@PathVariable Long userId,
                                             @RequestParam String finishType,
                                             @RequestParam String category,
                                             @RequestParam String subGoalType,
                                             @RequestParam String sortType) {
        return new BaseResponse<>(goalService.getArchiveListResponse(userId, finishType, category, subGoalType, sortType));
    }

    @GetMapping("/{goalId}")
    public GoalResponseDto getArchive(@PathVariable Long goalId) {
        return goalService.getGoalResponseByGoalId(goalId);
    }

}
