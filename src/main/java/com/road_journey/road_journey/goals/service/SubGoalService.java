package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.Goal;
import com.road_journey.road_journey.goals.domain.SubGoal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.repository.SubGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubGoalService {

    @Autowired
    private SubGoalRepository subGoalRepository;

    public List<SubGoal> createSubGoalList(Goal goal, AddGoalRequestDto addGoalRequestDto) {
        if (addGoalRequestDto.getSubGoalType().equals("normal")) {
            return null;
        }

        List<SubGoal> subGoalList = new ArrayList<>();
        for (AddGoalRequestDto.SubGoal subGoalRequest : addGoalRequestDto.getSubGoalList()) {
            subGoalList.add(createSubGoal(goal, subGoalRequest));
        }
        return subGoalList;
    }

    public SubGoal createSubGoal(Goal goal, AddGoalRequestDto.SubGoal subGoalRequest) {
        SubGoal subGoal = SubGoal.builder()
                .goal(goal)
                .subGoalIndex(subGoalRequest.getIndex())
                .description(subGoalRequest.getDescription())
                .isCompleted(false)
                .difficulty(subGoalRequest.getDifficulty())
                .progressStatus("none") // TODO 상태값 수정 필요
                .status("none")
                .build();
        //subGoalRepository.save(subGoal);
        return subGoal;
    }

    public List<SubGoal> getSubGoalsByGoalId(Long goalId) {
        return subGoalRepository.findByGoalId(goalId);
    }
}
