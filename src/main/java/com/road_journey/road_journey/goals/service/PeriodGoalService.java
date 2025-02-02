package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.PeriodGoal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.repository.PeriodGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeriodGoalService {

    @Autowired
    private PeriodGoalRepository periodGoalRepository;

    public void createPeriodGoal(Long goalId, AddGoalRequestDto.DateInfo dateInfo) {
        PeriodGoal periodGoal = PeriodGoal.builder()
                .goalId(goalId)
                .startAt(dateInfo.getStartAt().atStartOfDay())
                .expireAt(dateInfo.getExpireAt().atStartOfDay())
                .completedAt(null)
                .status("none")
                .build();
        periodGoalRepository.save(periodGoal);
    }
}
