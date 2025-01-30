package com.road_journey.road_journey.goals.repository;

import com.road_journey.road_journey.goals.domain.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {

}
