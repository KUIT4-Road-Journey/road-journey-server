package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.PeriodGoal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.repository.PeriodGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PeriodGoalService {

    @Autowired
    private PeriodGoalRepository periodGoalRepository;

    public void createPeriodGoal(Long goalId, AddGoalRequestDto.DateInfo dateInfo) {

        LocalDate startAt = dateInfo.getStartAt();
        LocalDate expireAt = dateInfo.getExpireAt();
        int repetitionNumber = dateInfo.getRepetitionNumber();
        int repetitionPeriod = dateInfo.getRepetitionPeriod();

        LocalDate periodStartAt;
        LocalDate periodExpireAt;

        if (startAt != null) {
            periodStartAt = startAt;
            periodExpireAt = expireAt;
        }
        else {
            startAt = LocalDate.now();
            expireAt = startAt.plusDays((long) repetitionNumber * repetitionPeriod);
            periodStartAt = startAt;
            periodExpireAt = startAt.plusDays(repetitionPeriod);;
        }

        PeriodGoal periodGoal = PeriodGoal.builder()
                .goalId(goalId)
                .startAt(startAt)
                .expireAt(expireAt)
                .periodStartAt(periodStartAt)
                .periodExpireAt(periodExpireAt)
                .completedAt(null)
                .status("none")
                .build();
        periodGoalRepository.save(periodGoal);
    }
}
