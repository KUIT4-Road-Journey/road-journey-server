package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.Goal;
import com.road_journey.road_journey.goals.domain.PeriodGoal;
import com.road_journey.road_journey.goals.domain.RepeatedGoal;
import com.road_journey.road_journey.goals.domain.SubGoal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;
import com.road_journey.road_journey.goals.dto.GoalResponseDto;
import com.road_journey.road_journey.goals.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        if (addGoalRequest.isSharedGoal()) { // 친구 사용자들의 목표 생성
            for (AddGoalRequestDto.Friend friend : addGoalRequest.getFriendList()) {
                createGoalOfUser(addGoalRequest, friend.getUserId(), userGoalId);
            }
        }
    }

    private Long createGoalOfUser(AddGoalRequestDto addGoalRequest, Long userId, Long originalGoalId) {

        Goal goal = createGoalByRequest(userId, addGoalRequest, originalGoalId);
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

    private String getInitialSharedStatus(AddGoalRequestDto addGoalRequest, Long originalGoalId) {
        if (originalGoalId == null) { // 사용자 본인이 생성한 목표
            if (addGoalRequest.isSharedGoal()) { // 공유 목표인 경우
                return "accepted"; // 수락
            }
            return "registered"; // 등록
        }
        // 공동 목표 요청을 받은 경우
        return "pending"; // 대기중
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

    private Goal createGoalByRequest(Long userId, AddGoalRequestDto addGoalRequest, Long originalGoalId) {
        return Goal.builder()
                .userId(userId)
                .originalGoalId(null)
                .existingGoalId(null)
                .title(addGoalRequest.getTitle())
                .difficulty(addGoalRequest.getDifficulty())
                .description(addGoalRequest.getDescription())
                .category(addGoalRequest.getCategory())
                .isSharedGoal(addGoalRequest.isSharedGoal())
                .isPublic(addGoalRequest.isPublicGoal())
                .subGoalType(addGoalRequest.getSubGoalType())
                .isRewarded(false)
                .sharedStatus(this.getInitialSharedStatus(addGoalRequest, originalGoalId))
                .progressStatus("none") // TODO 상태값 수정 필요
                .finishedAt(null)
                .status("activated") // TODO 문자열 하드코딩 처리
                .build();
    }

    public GoalResponseDto getGoalResponse(Long goalId) {
        Optional<Goal> goal = getGoalById(goalId);
        Optional<PeriodGoal> periodGoal = periodGoalService.getPeriodGoalByGoalId(goalId);
        Optional<RepeatedGoal> repeatedGoal = repeatedGoalService.getRepeatedGoalByGoalId(goalId);
        List<SubGoal> subGoalList = subGoalService.getSubGoalsByGoalId(goalId);
        List<Long> friendIdList = getFriendIdList(goal.get().getOriginalGoalId());

        return new GoalResponseDto(goal.get(), periodGoal, repeatedGoal, subGoalList, friendIdList);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    public List<Goal> getGoalSByOriginalGoalId(Long originalGoalId) {
        return goalRepository.findGoalsByOriginalGoalId(originalGoalId);
    }

    public List<Long> getFriendIdList(Long originalGoalId) {
        List<Goal> goalList = goalRepository.findGoalsByOriginalGoalId(originalGoalId);
        List<Long> friendIdList = new ArrayList<>();

        for (Goal goal : goalList) {
            friendIdList.add(goal.getUserId());
        }
        return friendIdList;
    }

}
