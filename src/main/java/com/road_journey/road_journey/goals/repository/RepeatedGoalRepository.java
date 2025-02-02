package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatedGoalRepository extends JpaRepository<RepeatedGoal, Long> {
}
