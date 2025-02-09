package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.goals.domain.*;
import com.road_journey.road_journey.goals.dto.*;
import com.road_journey.road_journey.goals.repository.GoalRepository;
import com.road_journey.road_journey.goals.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ResponseStatus createGoals(AddGoalRequestDto addGoalRequest) {
        Long userId = 12345L; // TODO 실제 사용자 아이디로 변경

        Long userGoalId = createGoalOfUser(addGoalRequest, userId, null); // 사용자 본인의 목표 생성

        if (addGoalRequest.isSharedGoal()) { // 친구 사용자들의 목표 생성
            for (AddGoalRequestDto.Friend friend : addGoalRequest.getFriendList()) {
                createGoalOfUser(addGoalRequest, friend.getUserId(), userGoalId);
            }
        }
        return new BaseResponse<>("Goals added.");
    }

    private Long createGoalOfUser(AddGoalRequestDto addGoalRequest, Long userId, Long originalGoalId) {

        Goal goal = createGoalByRequest(userId, addGoalRequest, originalGoalId);

        goal.setPeriodGoal(periodGoalService.createPeriodGoal(goal, addGoalRequest.getDateInfo()));
        goal.setRepeatedGoal(repeatedGoalService.createRepeatedGoal(goal, addGoalRequest));
        goal.setSubGoalList(subGoalService.createSubGoalList(goal, addGoalRequest));

        return saveGoalWithOriginalGoalId(goal, originalGoalId); // 생성한 목표 아이디 반환
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

    public GoalResponseDto getGoalResponseByGoalId(Long goalId) {
        Optional<Goal> goal = getGoalById(goalId);
        return goal.map(this::getGoalResponse).orElse(null); // 목표를 찾지 못하면 null 반환
    }

    public GoalResponseDto getGoalResponse(Goal goal) {
        List<Long> friendIdList = getFriendIdList(goal.getOriginalGoalId());
        return GoalResponseDto.from(goal, friendIdList);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    public List<Goal> findSharedGoals(Long originalGoalId) {
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

    public GoalListResponseDto getGoalListResponse(Long userId, String category) {
        // TODO 목표 리스트에 출력되는 정확한 기준 반영 필요
        List<Goal> goalList = goalRepository.findGoalsByUserIdAndCategoryAndStatus(userId, category, "activated");

        goalList.removeIf(goal -> !goal.isIncludedInList()); // 리스트에 출력할 목표들만 남기기

        List<GoalResponseDto> goalResponseList = goalList.stream()
                .map(this::getGoalResponse)
                .collect(Collectors.toList());
        return new GoalListResponseDto(goalResponseList);
    }


    public ResponseStatus editGoals(Long goalId, AddGoalRequestDto addGoalRequest) {
        Optional<Goal> optionalGoal = goalRepository.findById(goalId);
        if (optionalGoal.isEmpty()) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND, "No goal with goalId");
        }
        Goal goal = optionalGoal.get();

        if (!goal.isMadeByUser()) { // 사용자 본인이 생성한 목표인지 확인
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST, "Not the owner of the goal");
        }
        if (goal.getExistingGoalId() != null) { // 원본 목표가 아닌 경우 (수정승인 대기중)
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST, "Edit pending goal");
        }

        Long userGoalId = editGoalOfUser(goal, addGoalRequest, null);
        for (Goal sharedGoal : findSharedGoals(goal.getOriginalGoalId())) {
            if (goal != sharedGoal) {
                editGoalOfUser(sharedGoal, addGoalRequest, userGoalId);
            }
        }
        return new BaseResponse<>("Goals edited.");
    }

    private Long editGoalOfUser(Goal goal, AddGoalRequestDto addGoalRequest, Long originalGoalId) {
        List<Goal> editPendingGoalList = getEditPendingGoal(goal.getGoalId());
        for (Goal editPendingGoal : editPendingGoalList) {
            editPendingGoal.deactivate(); // 기존에 수정 승인 대기 중인 목표를 찾아 비활성화
            goalRepository.save(editPendingGoal);
        }

        Goal editedGoal = editGoalByRequest(goal, addGoalRequest, originalGoalId);

        PeriodGoal periodGoal;
        RepeatedGoal repeatedGoal;
        List<SubGoal> subGoalList;

        if (goal.isStarted()) { // 이미 시작한 목표 -> 기존 값 복사
            periodGoal = periodGoalService.copyPeriodGoal(editedGoal, goal.getPeriodGoal());
            repeatedGoal = repeatedGoalService.copyRepeatedGoal(editedGoal, goal.getRepeatedGoal());
            subGoalList = subGoalService.copySubGoalList(editedGoal, goal.getSubGoalList());
        } else { // 아직 시작하지 않은 목표 -> 새로운 값으로 생성
            periodGoal = periodGoalService.createPeriodGoal(editedGoal, addGoalRequest.getDateInfo());
            repeatedGoal = repeatedGoalService.createRepeatedGoal(editedGoal, addGoalRequest);
            subGoalList = subGoalService.createSubGoalList(editedGoal, addGoalRequest);
        }
        editedGoal.setPeriodGoal(periodGoal);
        editedGoal.setRepeatedGoal(repeatedGoal);
        editedGoal.setSubGoalList(subGoalList);
        return saveGoalWithOriginalGoalId(editedGoal, originalGoalId); // 생성한 목표 아이디 반환
    }

    private Goal editGoalByRequest(Goal goal, AddGoalRequestDto addGoalRequest, Long originalGoalId) {
        Goal editedGoal = Goal.builder()
                .userId(goal.getUserId())
                .originalGoalId(null)
                .existingGoalId(goal.getGoalId())
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

        if (goal.isStarted()) {
            editedGoal.setCategory(goal.getCategory());
            editedGoal.setSharedGoal(goal.isSharedGoal());
            editedGoal.setSubGoalType(goal.getSubGoalType());
        }
        return editedGoal;
    }

    private List<Goal> getEditPendingGoal(Long existingGoalId) {
        return goalRepository.findGoalsByExistingGoalId(existingGoalId);
    }
}
