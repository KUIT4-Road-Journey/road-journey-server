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
        return SubGoal.builder()
                .goal(goal)
                .subGoalIndex(subGoalRequest.getIndex())
                .description(subGoalRequest.getDescription())
                .difficulty(subGoalRequest.getDifficulty())
                .progressStatus("progressing") // TODO 상태값 수정 필요
                .status("activated")
                .build();
    }

    public List<SubGoal> copySubGoalList(Goal goal, List<SubGoal> subGoalList) {
        List<SubGoal> newSubGoalList = new ArrayList<>();
        for (SubGoal subGoal : subGoalList) {
            newSubGoalList.add(copySubGoal(goal, subGoal));
        }
        return newSubGoalList;
    }

    public SubGoal copySubGoal(Goal goal, SubGoal subGoal) {
        return SubGoal.builder()
                .goal(goal)
                .subGoalIndex(subGoal.getSubGoalIndex())
                .description(subGoal.getDescription())
                .difficulty(subGoal.getDifficulty())
                .progressStatus(subGoal.getProgressStatus())
                .status(subGoal.getStatus())
                .build();
    }

    public List<SubGoal> getSubGoalsByGoalId(Long goalId) {
        return subGoalRepository.findByGoalId(goalId);
    }
}
