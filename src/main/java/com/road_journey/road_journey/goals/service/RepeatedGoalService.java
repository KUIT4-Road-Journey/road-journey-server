package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.Goal;
import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.repository.RepeatedGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RepeatedGoalService {

    @Autowired
    private RepeatedGoalRepository repeatedGoalRepository;

    public RepeatedGoal createRepeatedGoal(Goal goal, AddGoalRequestDto addGoalRequestDto) {
        if (!addGoalRequestDto.isRepeatedGoal()) {
            return null;
        }
        AddGoalRequestDto.DateInfo dateInfo = addGoalRequestDto.getDateInfo();
        return RepeatedGoal.builder()
                .goal(goal)
                .repetitionPeriod(dateInfo.getRepetitionPeriod())
                .repetitionNumber(dateInfo.getRepetitionNumber())
                .completedCount(0)
                .failedCount(0)
                .repetitionHistory("")
                .status("activated")
                .build();
    }

    public RepeatedGoal copyRepeatedGoal(Goal goal, RepeatedGoal repeatedGoal) {
        if (repeatedGoal == null) {
            return null;
        }
        return RepeatedGoal.builder()
                .goal(goal)
                .repetitionPeriod(repeatedGoal.getRepetitionPeriod())
                .repetitionNumber(repeatedGoal.getRepetitionNumber())
                .completedCount(repeatedGoal.getCompletedCount())
                .failedCount(repeatedGoal.getFailedCount())
                .status(repeatedGoal.getStatus())
                .build();
    }

    public Optional<RepeatedGoal> getRepeatedGoalByGoalId(Long goalId) {
        return repeatedGoalRepository.findByGoalId(goalId);
    }
}
