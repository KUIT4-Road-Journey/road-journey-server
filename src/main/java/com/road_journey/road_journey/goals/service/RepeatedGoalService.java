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
        RepeatedGoal repeatedGoal = RepeatedGoal.builder()
                .goal(goal)
                .repetitionPeriod(dateInfo.getRepetitionPeriod())
                .repetitionNumber(dateInfo.getRepetitionNumber())
                .completedCount(0)
                .failedCount(0)
                .status("none")
                .build();
        //repeatedGoalRepository.save(repeatedGoal);
        return repeatedGoal;
    }

    public Optional<RepeatedGoal> getRepeatedGoalByGoalId(Long goalId) {
        return repeatedGoalRepository.findByGoalId(goalId);
    }
}
