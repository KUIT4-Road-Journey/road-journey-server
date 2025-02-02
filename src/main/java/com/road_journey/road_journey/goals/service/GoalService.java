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

    @Autowired
    private SubGoalService subGoalService;
    @Autowired
    private RepeatedGoalService repeatedGoalService;
    @Autowired
    private PeriodGoalService periodGoalService;

    public void createGoals(AddGoalRequestDto addGoalRequest) {
        Long userId = 12345L; // TODO 실제 사용자 아이디로 변경

        Long userGoalId = createGoalOfUser(addGoalRequest, userId, null); // 사용자 본인의 목표 생성

        for (AddGoalRequestDto.Friend friend : addGoalRequest.getFriendList()) { // 친구 사용자들의 목표 생성
            createGoalOfUser(addGoalRequest, friend.getUserId(), userGoalId);
        }
    }

    private Long createGoalOfUser(AddGoalRequestDto addGoalRequest, Long userId, Long originalGoalId) {
        Goal goal = createGoalByRequest(userId, addGoalRequest);
        Long userGoalId = saveGoalWithOriginalGoalId(goal, originalGoalId);

        if (addGoalRequest.isRepeatedGoal()) { // 반복 목표인 경우
            repeatedGoalService.createRepeatedGoal(userGoalId, addGoalRequest.getDateInfo()); //RepeatedGoal 객체 생성
        }
        periodGoalService.createPeriodGoal(userGoalId, addGoalRequest.getDateInfo()); // PeriodGoal 객체 생성

        // 하위목표 객체 생성
        for (AddGoalRequestDto.SubGoal subGoalRequest : addGoalRequest.getSubGoalList()) {
            subGoalService.createSubGoal(userGoalId, subGoalRequest);
        }
        return userGoalId;
    }

    private Long saveGoalWithOriginalGoalId(Goal goal, Long OriginalGoalId) {
        if (OriginalGoalId == null) { // 자기 자신이 원본
            goal = goalRepository.save(goal);
            OriginalGoalId = goal.getGoalId();
        }
        // 원본이 따로 존재하는 경우
        goal.setOriginalGoalId(OriginalGoalId);
        goalRepository.save(goal);

        return goal.getGoalId();
    }

    private static Goal createGoalByRequest(Long userId, AddGoalRequestDto addGoalRequest) {
        return Goal.builder()
                .userId(userId)
                .originalGoalId(12345L)
                .title(addGoalRequest.getTitle())
                .difficulty(addGoalRequest.getDifficulty())
                .description(addGoalRequest.getDescription())
                .isSharedGoal(addGoalRequest.isSharedGoal())
                .isPublic(addGoalRequest.isPublicGoal())
                .subGoalType(addGoalRequest.getSubGoalType())
                .isRewarded(false)
                .sharedStatus("none") // TODO 상태 추가
                .completedStatus(1)
                .finishedAt(null)
                .status("none") // TODO 상태 추가
                .build();
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

}
