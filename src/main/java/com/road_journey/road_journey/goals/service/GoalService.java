package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.archives.dto.ArchiveListResponseDto;
import com.road_journey.road_journey.goals.domain.*;
import com.road_journey.road_journey.goals.dto.*;
import com.road_journey.road_journey.goals.repository.GoalRepository;
import com.road_journey.road_journey.goals.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
                .sharedGoalType(addGoalRequest.getSharedGoalType())
                .isPublic(addGoalRequest.isPublicGoal())
                .subGoalType(addGoalRequest.getSubGoalType())
                .rewardCount(0)
                .sharedStatus(this.getInitialSharedStatus(addGoalRequest, originalGoalId))
                .progressStatus("progressing") // TODO 상태값 수정 필요
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

    public List<Goal> getSharedGoalList(Long originalGoalId) {
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
        List<Goal> goalList = goalRepository.findGoalsByUserIdAndCategoryAndStatus(userId, category, "activated");
        goalList.removeIf(goal -> !goal.isIncludedInGoalList()); // 리스트에 출력할 목표들만 남기기
        // TODO 본인 목표인지, 친구 목표인지, 공개 여부 등 반영
        return new GoalListResponseDto(goalList);
    }

    public ArchiveListResponseDto getArchiveListResponse(Long userId, String finishType, String category, String subGoalType, String sortType) {
        List<Goal> goalList = goalRepository.findGoalsByUserIdAndCategoryAndSubGoalTypeAndStatus(userId, category, subGoalType, "activated");
        goalList.removeIf(goal -> !goal.isIncludedInArchiveList(finishType)); // 리스트에 출력할 목표들만 남기기

        if (sortType.equals("lastCreated")) {
            goalList.sort(Comparator.comparing(Goal::getCreatedAt).reversed());
        } else {
            goalList.sort(Comparator.comparing(Goal::getFinishedAt).reversed());
        }
        return new ArchiveListResponseDto(goalList);
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
        for (Goal sharedGoal : getSharedGoalList((goal.getOriginalGoalId()))) {
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
                .sharedGoalType(addGoalRequest.getSharedGoalType())
                .isPublic(addGoalRequest.isPublicGoal())
                .subGoalType(addGoalRequest.getSubGoalType())
                .rewardCount(goal.getRewardCount())
                .sharedStatus(this.getInitialSharedStatus(addGoalRequest, originalGoalId))
                .progressStatus("progressing") // TODO 상태값 수정 필요
                .finishedAt(null)
                .status("activated") // TODO 문자열 하드코딩 처리
                .build();

        if (goal.isStarted()) {
            editedGoal.setCategory(goal.getCategory());
            editedGoal.setSharedGoal(goal.isSharedGoal());
            editedGoal.setSharedGoalType(goal.getSharedGoalType());
            editedGoal.setSubGoalType(goal.getSubGoalType());
            editedGoal.setProgressStatus(goal.getProgressStatus());
        }
        return editedGoal;
    }

    private List<Goal> getEditPendingGoal(Long existingGoalId) {
        return goalRepository.findGoalsByExistingGoalId(existingGoalId);
    }


    public ResponseStatus processPendingGoal(Long goalId, boolean isCreation, boolean isAccept) {
        Optional <Goal> optionalGoal = getGoalById(goalId);
        if (optionalGoal.isEmpty()) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        Goal goal = optionalGoal.get();

        // TODO 본인의 목표가 맞는지 확인

        if (!goal.isPending()) { // 승인 대기 중인 목표가 아닌 경우
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }

        if (isAccept) {
            return acceptGoal(goal, isCreation);
        }
        return rejectGoal(goal, isCreation);
    }

    private BaseResponse<Object> acceptGoal(Goal goal, boolean isCreation) {
        goal.accept(); // 공동 상태를 '수락'으로 변경
        goalRepository.save(goal);

        // 공동 목표들을 확인하여 모두 수락되어 있으면 공동 상태를 '등록'으로 변경
        List<Goal> sharedGoalList = getSharedGoalList(goal.getOriginalGoalId());
        if (isAllAccepted(sharedGoalList)) {
            registerSharedGoals(sharedGoalList);
        }

        if (isCreation) {
            return new BaseResponse<>("Goal " + goal.getGoalId() + " accepted");
        }
        return new BaseResponse<>("Goal edition " + goal.getGoalId() + " accepted");
    }

    private ResponseStatus rejectGoal(Goal goal, boolean isCreation) {
        goal.reject();
        goalRepository.save(goal);

        // 공동 목표들을 확인하여 모두 비활성화
        List<Goal> sharedGoalList = getSharedGoalList(goal.getOriginalGoalId());
        for (Goal sharedGoal : sharedGoalList) {
            sharedGoal.deactivate();
            goalRepository.save(sharedGoal);
        }
        if (isCreation) {
            return new BaseResponse<>("Goal " + goal.getGoalId() + " rejected");
        }
        return new BaseResponse<>("Goal edition " + goal.getGoalId() + " rejected");
    }

    private boolean isAllAccepted(List<Goal> sharedGoalList) {
        for (Goal sharedGoal : sharedGoalList) {
            if (!sharedGoal.getSharedStatus().equals("accepted")){
                return false;
            }
        }
        return true; // 모든 공동 목표들이 수락되어 있는지 확인
    }

    private void registerSharedGoals(List<Goal> sharedGoalList) {
        for (Goal sharedGoal : sharedGoalList) {
            if (sharedGoal.isPendingForEdit()) { // 수정 대기 중인 경우
                deactivateExistingGoal(sharedGoal); // 수정 전 원본 목표를 비활성화
            }
            sharedGoal.register();
            goalRepository.save(sharedGoal);
        }
    }

    private void deactivateExistingGoal(Goal sharedGoal) {
        Optional<Goal> optionalExistingGoal = getGoalById(sharedGoal.getExistingGoalId());
        if (optionalExistingGoal.isPresent()){
            Goal existingGoal = optionalExistingGoal.get();
            existingGoal.deactivate(); // 수정 전 원본 목표를 찾아 비활성화
            goalRepository.save(existingGoal);
        }
        sharedGoal.setExistingGoalId(null);
    }


    public ResponseStatus completeGoal(Long goalId) {
        Optional <Goal> optionalGoal = getGoalById(goalId);
        if (optionalGoal.isEmpty()) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        Goal goal = optionalGoal.get();

        // TODO 본인의 목표가 맞는지 확인

        if (!goal.isCompletable()) {
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }

        if (!goal.isSharedGoal()) { // 혼자 하는 목표
            goal.complete(); // 바로 달성 완료 처리
            goalRepository.save(goal);
        }
        else { // 공동 목표인 경우
            if (goal.getSharedGoalType().equals("competitive")) { // 경쟁 목표인 경우
                for (Goal sharedGoal : getSharedGoalList(goal.getOriginalGoalId())) {
                    if (sharedGoal != goal) {
                        sharedGoal.fail(); // 나머지 공동 목표들은 모두 실패 처리
                        goalRepository.save(sharedGoal);
                    }
                }
                goal.complete(); // 바로 달성 완료 처리
                goalRepository.save(goal);
            }
            else { // 협동 목표인 경우
                goal.setProgressStatus("complete-pending"); // 달성 대기 중으로 변경
                goalRepository.save(goal);

                List<Goal> sharedGoal = getSharedGoalList(goal.getOriginalGoalId());
                if (isAllCompletePending(sharedGoal)) { // 모두 달성 대기 중인 경우
                    completeSharedGoals(sharedGoal); // 모두 달성 완료 상태로 변경
                }
            }
        }

        if (goal.getProgressStatus().equals("completed")) {
            return getRewardOfGoal(goal);
        }

        return new BaseResponse<>("Goal " + goalId + " completed");
    }

    public boolean isAllCompletePending(List<Goal> sharedGoalList) {
        // 모든 공동 목표가 달성 완료 대기 중인지 확인
        for (Goal sharedGoal : sharedGoalList) {
            if (!sharedGoal.getProgressStatus().equals("complete-pending")) {
                return false;
            }
        }
        return true;
    }

    public void completeSharedGoals(List<Goal> sharedGoalList) {
        // 모든 공동 목표를 달성 완료 처리
        for (Goal sharedGoal : sharedGoalList) {
            sharedGoal.complete();
            goalRepository.save(sharedGoal);
        }
    }

    public ResponseStatus failGoal(Long goalId) {
        Optional <Goal> optionalGoal = getGoalById(goalId);
        if (optionalGoal.isEmpty()) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        Goal goal = optionalGoal.get();

        // TODO 본인의 목표가 맞는지 확인

        if (!goal.isFailable()) {
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }

        if (!goal.isSharedGoal()) { // 혼자 하는 목표
            goal.fail(); // 바로 달성 실패 처리
            goalRepository.save(goal);
        }
        else { // 공동 목표인 경우
            if (goal.getSharedGoalType().equals("competitive")) { // 경쟁 목표인 경우
                goal.fail(); // 바로 달성 실패 처리
                goalRepository.save(goal);
            }
            else { // 협동 목표인 경우
                List<Goal> sharedGoal = getSharedGoalList(goal.getOriginalGoalId());
                failSharedGoals(sharedGoal);
            }
        }
        return getRewardOfGoal(goal);
    }

    public void failSharedGoals(List<Goal> sharedGoalList) {
        // 모든 공동 목표를 달성 실패 처리
        for (Goal sharedGoal : sharedGoalList) {
            sharedGoal.fail();
            goalRepository.save(sharedGoal);
        }
    }

    public ResponseStatus completeSubGoal(Long goalId, Long subGoalId) {
        Optional <Goal> optionalGoal = getGoalById(goalId);
        if (optionalGoal.isEmpty()) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        Goal goal = optionalGoal.get();
        SubGoal subGoal = goal.getSubGoalById(subGoalId);
        if (subGoal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }

        // TODO 본인의 목표가 맞는지 확인

        if (!goal.isSubGoalCompletable(subGoal.getSubGoalIndex())) { // 달성 처리 가능한지 확인
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }
        subGoal.setProgressStatus("completed"); // 하위 목표를 달성 처리
        goalRepository.save(goal);
        // TODO 사용자의 골드 /성장도 반영 필요
        return new BaseResponse<>(new GoalRewardResponseDto(goal));
    }

    public ResponseStatus getRewardByGoalId(Long goalId) {
        Optional <Goal> optionalGoal = getGoalById(goalId);
        if (optionalGoal.isEmpty()) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        Goal goal = optionalGoal.get();

        // TODO 본인의 목표가 맞는지 확인

        return getRewardOfGoal(goal);
    }

    public ResponseStatus getRewardOfGoal(Goal goal) {
        if (!goal.canGetReward()) {
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }
        goal.setProgressStatus("progressing");
        goal.setRewardCount(goal.getRewardCount() + 1);
        goal.setFinishedAt(LocalDateTime.now());
        goal.updatePeriodDate();
        goalRepository.save(goal);
        // TODO 사용자의 골드 /성장도 반영 필요
        return new BaseResponse<>(new GoalRewardResponseDto(goal));
    }

    // TODO subGoal - isCompleted / periodGoal - completedAt / repeatedGoal - completedCount, failedCount 필요 없으면 제거
}
