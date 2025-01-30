package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.Goal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    public void createGoal(AddGoalRequestDto addGoalRequest) {
        Goal goal = Goal.builder()
                .userId(12345L)
                .originalGoalId(12345L)
                .title(addGoalRequest.getTitle())
                .difficulty(addGoalRequest.getDifficulty())
                .description(addGoalRequest.getDescription())
                .isSharedGoal(addGoalRequest.isSharedGoal())
                .isPublic(addGoalRequest.isPublicGoal())
                .subGoalType(addGoalRequest.getSubGoalType())
                .isRewarded(false)
                .sharedStatus("none")
                .completedStatus(1)
                .finishedAt(null)
                .status("none")
                .createdAt(null)
                .updatedAt(null)
                .build();

        goalRepository.save(goal);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

}
