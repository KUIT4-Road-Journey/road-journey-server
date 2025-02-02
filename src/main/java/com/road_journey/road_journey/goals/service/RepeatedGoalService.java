package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.repository.RepeatedGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepeatedGoalService {

    @Autowired
    private RepeatedGoalRepository repeatedGoalRepository;

    public void createRepeatedGoal(Long goalId, AddGoalRequestDto.DateInfo dateInfo) {
        RepeatedGoal repeatedGoal = RepeatedGoal.builder()
                .goalId(goalId)
                .period(dateInfo.getRepetitionPeriod())
                .number(dateInfo.getRepetitionNumber())
                .completedCount(0)
                .failedCount(0)
                .status("none")
                .build();
        repeatedGoalRepository.save(repeatedGoal);
    }
}
