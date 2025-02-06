package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.PeriodGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeriodGoalRepository extends JpaRepository<PeriodGoal, Long> {
    Optional<PeriodGoal> findByGoalId(Long goalId);
}
