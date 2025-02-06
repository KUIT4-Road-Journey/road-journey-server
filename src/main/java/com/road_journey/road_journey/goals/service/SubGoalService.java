package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.SubGoal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.repository.SubGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubGoalService {

    @Autowired
    private SubGoalRepository subGoalRepository;

    public void createSubGoal(Long goalId, AddGoalRequestDto.SubGoal subGoalRequest) {
        SubGoal subGoal = SubGoal.builder()
                .goalId(goalId)
                .subGoalIndex(subGoalRequest.getIndex())
                .description(subGoalRequest.getDescription())
                .isCompleted(false)
                .difficulty(subGoalRequest.getDifficulty())
                .progressStatus("none") // TODO 상태값 수정 필요
                .status("none")
                .build();
        subGoalRepository.save(subGoal);
    }

    public List<SubGoal> getSubGoalsByGoalId(Long goalId) {
        return subGoalRepository.findByGoalId(goalId);
    }
}
