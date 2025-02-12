package com.road_journey.road_journey.goals.service;

import com.road_journey.road_journey.archives.dto.ArchiveListResponseDto;
import com.road_journey.road_journey.goals.domain.*;
import com.road_journey.road_journey.goals.dto.*;
import com.road_journey.road_journey.goals.repository.GoalRepository;
import com.road_journey.road_journey.goals.response.*;
import com.road_journey.road_journey.goals.util.GoalUtil;
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
                // TODO 공동목표 생성에 대한 수락 요청 메세지 전송
            }
        }
        return new BaseResponse<>("Goals added.");
    }

    private Long createGoalOfUser(AddGoalRequestDto addGoalRequest, Long userId, Long originalGoalId) {
        Goal goal = GoalUtil.createGoalByRequest(userId, addGoalRequest, originalGoalId);

        goal.setPeriodGoal(periodGoalService.createPeriodGoal(goal, addGoalRequest.getDateInfo()));
        goal.setRepeatedGoal(repeatedGoalService.createRepeatedGoal(goal, addGoalRequest));
        goal.setSubGoalList(subGoalService.createSubGoalList(goal, addGoalRequest));

        return saveGoalWithOriginalGoalId(goal, originalGoalId); // 생성한 목표 아이디 반환
    }

    private Long saveGoalWithOriginalGoalId(Goal goal, Long OriginalGoalId) {
        // 생성된 목표 아이디를 원본 목표 아이디에 적용하여 다시 저장

        if (OriginalGoalId == null) { // 자기 자신이 원본인 경우
            goal = goalRepository.save(goal); // 일단 원본 목표 아이디 없이 저장
            OriginalGoalId = goal.getGoalId(); // 저장 후 생성된 자신의 아이디를 원본 목표 아이디로 사용
        }
        goal.setOriginalGoalId(OriginalGoalId);
        goalRepository.save(goal);

        return goal.getGoalId();
    }

    public ResponseStatus getGoalResponseByGoalId(Long goalId) {
        Optional<Goal> optionalGoal = getGoalById(goalId);
        if (optionalGoal.isEmpty()) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        return new BaseResponse<>(getGoalResponse(optionalGoal.get()));
    }

    public GoalResponseDto getGoalResponse(Goal goal) {
        return GoalResponseDto.from(goal, getFriendIdList(goal.getOriginalGoalId()));
    }

    public ResponseStatus getGoalListResponse(Long userId, String category) {
        // TODO 본인이거나, 친구가 아닌 userId이면 바로 리턴

        List<Goal> goalList = goalRepository.findGoalsByUserIdAndCategoryAndStatus(userId, category, "activated");
        goalList.removeIf(goal -> !goal.isIncludedInGoalList()); // 목표 리스트에 출력할 목표들만 남기기
        // TODO 친구의 목표 리스트를 조회하는 경우, 공개 설정하지 않은 목표도 리스트에서 제거

        return new BaseResponse<>(new GoalListResponseDto(goalList));
    }

    public ResponseStatus getArchiveListResponse(Long userId, String finishType, String category, String subGoalType, String sortType) {
        List<Goal> goalList = goalRepository.findGoalsByUserIdAndCategoryAndSubGoalTypeAndStatus(userId, category, subGoalType, "activated");
        goalList.removeIf(goal -> !goal.isIncludedInArchiveList(finishType)); // 기록 리스트에 출력할 목표들만 남기기

        if (sortType.equals("lastCreated")) { // 정렬 기준에 따라 정렬
            goalList.sort(Comparator.comparing(Goal::getCreatedAt).reversed());
        } else {
            goalList.sort(Comparator.comparing(Goal::getFinishedAt).reversed());
        }
        return new BaseResponse<>(new ArchiveListResponseDto(goalList));
    }


    public ResponseStatus editGoals(Long goalId, AddGoalRequestDto addGoalRequest) {
        Goal goal = getAuthorizedGoal(goalId);
        if (goal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        if (!goal.isMadeByUser()) { // 사용자 본인이 생성한 목표인지 확인
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST, "Not the owner of the goal.");
        }
        if (goal.getExistingGoalId() != null) { // 원본 목표가 아닌 경우 (수정승인 대기중)
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST, "Edit pending goal.");
        }

        Long userGoalId = editGoalOfUser(goal, addGoalRequest, null);
        for (Goal sharedGoal : getSharedGoalList((goal.getOriginalGoalId()))) {
            if (goal != sharedGoal) {
                editGoalOfUser(sharedGoal, addGoalRequest, userGoalId);
                // TODO 공동목표 수정에 대한 수락 요청 메세지 전송
            }
        }
        return new BaseResponse<>("Goals edited.");
    }

    private Long editGoalOfUser(Goal goal, AddGoalRequestDto addGoalRequest, Long originalGoalId) {
        for (Goal editPendingGoal : getEditPendingGoal(goal.getGoalId())) {
            deactivateAndSave(editPendingGoal); // 기존에 수정 승인 대기 중인 목표를 찾아 모두 비활성화
        }

        Goal editedGoal = GoalUtil.editGoalByRequest(goal, addGoalRequest, originalGoalId); // 수정본 객체 생성

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

    public ResponseStatus processPendingGoal(Long goalId, boolean isCreation, boolean isAccept) { // 승인 or 거절 수행
        Goal goal = getAuthorizedGoal(goalId);
        if (goal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        if (!goal.isPending()) { // 승인 대기 중인 목표가 아닌 경우
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }

        if (isAccept) {
            return acceptGoal(goal, isCreation);
        }
        return rejectGoal(goal, isCreation);
    }

    private BaseResponse<Object> acceptGoal(Goal goal, boolean isCreation) {
        acceptAndSave(goal); // 공동 상태를 '수락'으로 변경

        // 공동 목표들을 확인하여 모두 수락되어 있으면 공동 상태를 '등록'으로 변경
        List<Goal> sharedGoalList = getSharedGoalList(goal.getOriginalGoalId());
        if (GoalUtil.isAllAccepted(sharedGoalList)) {
            registerSharedGoals(sharedGoalList);
        }
        return new BaseResponse<>("Goal " + (isCreation ? "" : "edition ") + goal.getGoalId() + " accepted.");
    }

    private ResponseStatus rejectGoal(Goal goal, boolean isCreation) {
        rejectAndSave(goal); // 공동 상태를 '거절'으로 변경

        for (Goal sharedGoal : getSharedGoalList(goal.getOriginalGoalId())) {
            deactivateAndSave(sharedGoal); // 공동 목표들을 찾아 모두 비활성화
        }
        return new BaseResponse<>("Goal " + (isCreation ? "" : "edition ") + goal.getGoalId() + " rejected.");
    }

    private void registerSharedGoals(List<Goal> sharedGoalList) {
        for (Goal sharedGoal : sharedGoalList) {
            if (sharedGoal.isPendingForEdit()) { // 수정 대기 중인 경우
                deactivateExistingGoal(sharedGoal); // 수정 전 원본 목표를 비활성화
            }
            registerAndSave(sharedGoal);
        }
    }

    private void deactivateExistingGoal(Goal goal) {
        Optional<Goal> optionalExistingGoal = getGoalById(goal.getExistingGoalId());
        optionalExistingGoal.ifPresent(this::deactivateAndSave); // 수정 전 원본 목표를 찾아 비활성화
        goal.setExistingGoalId(null);
        goalRepository.save(goal);
    }

    public ResponseStatus completeGoal(Long goalId) {
        Goal goal = getAuthorizedGoal(goalId);
        if (goal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        if (!goal.isCompletable()) {
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }

        if (!goal.isSharedGoal()) { // 혼자 하는 목표인 경우
            completeAndSave(goal); // 바로 달성 완료 처리
        }
        else { // 공동 목표인 경우
            if (goal.isCompetitive()) { // 경쟁 목표인 경우
                for (Goal sharedGoal : getSharedGoalList(goal.getOriginalGoalId())) {
                    if (sharedGoal != goal) {
                        failAndSave(sharedGoal); // 나머지 공동 목표들은 모두 실패 처리
                    }
                }
                completeAndSave(goal); // 바로 달성 완료 처리
            }
            else { // 협동 목표인 경우
                completePendAndSave(goal); // 달성 대기 중으로 변경
                List<Goal> sharedGoal = getSharedGoalList(goal.getOriginalGoalId());
                if (GoalUtil.isAllCompletePending(sharedGoal)) { // 공동목표들이 모두 달성 대기 중인 경우
                    completeSharedGoals(sharedGoal); // 전부 달성 완료 상태로 변경
                }
            }
        }

        if (goal.isCompleted()) { // 목표가 최종적으로 달성 완료 상태인 경우
            return getRewardOfGoal(goal, false); // 즉시 보상 수령
        }
        return new BaseResponse<>("Goal " + goalId + " completed");
    }

    public void completeSharedGoals(List<Goal> sharedGoalList) {
        for (Goal sharedGoal : sharedGoalList) {
            completeAndSave(sharedGoal); // 모든 공동 목표들을 달성 완료 처리
        }
    }

    public ResponseStatus failGoal(Long goalId) {
        Goal goal = getAuthorizedGoal(goalId);
        if (goal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        if (!goal.isFailable()) {
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }

        if (!goal.isSharedGoal()) { // 혼자 하는 목표인 경우
            failAndSave(goal); // 바로 달성 실패 처리
        }
        else { // 공동 목표인 경우
            if (goal.isCompetitive()) { // 경쟁 목표인 경우
                failAndSave(goal); // 바로 달성 실패 처리
            }
            else { // 협동 목표인 경우
                failSharedGoals(getSharedGoalList(goal.getOriginalGoalId())); // 모든 공동목표들을 달성 실패 처리
            }
        }
        return getRewardOfGoal(goal, false); // 달성 실패 페널티 획득
    }

    public void failSharedGoals(List<Goal> sharedGoalList) {
        for (Goal sharedGoal : sharedGoalList) {
            failAndSave(sharedGoal); // 모든 공동 목표를 달성 실패 처리
        }
    }

    public ResponseStatus completeSubGoal(Long goalId, Long subGoalId) {
        Goal goal = getAuthorizedGoal(goalId);
        if (goal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }
        SubGoal subGoal = goal.getSubGoalById(subGoalId);
        if (subGoal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }

        if (!goal.isSubGoalCompletable(subGoal.getSubGoalIndex())) { // 달성 처리 가능한지 확인
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
        }
        completeSubGoalAndSave(goal, subGoal); // 하위목표를 달성 완료 처리
        return getRewardOfGoal(goal, true); // 하위 목표에 대한 보상 수령
    }



    public ResponseStatus getRewardByGoalId(Long goalId) {
        Goal goal = getAuthorizedGoal(goalId);
        if (goal == null) {
            return new BaseErrorResponse(ResponseStatusType.NOT_FOUND);
        }

        return getRewardOfGoal(goal, false);
    }

    public ResponseStatus getRewardOfGoal(Goal goal, boolean isForSubGoal) {
        if (!isForSubGoal) { // 전체 목표에 해당하는 보상을 수령하는 경우
            if (!goal.canGetReward()) { // 목표에 대한 보상을 수령 가능한지 확인
                return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST);
            }
            finishPeriodAndSave(goal); // 목표의 현재 주기를 종료
        }

        // TODO 사용자의 골드 /성장도 반영 필요
        return new BaseResponse<>(new GoalRewardResponseDto(goal));
    }



    private Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    private List<Goal> getSharedGoalList(Long originalGoalId) {
        return goalRepository.findGoalsByOriginalGoalId(originalGoalId);
    }

    private List<Long> getFriendIdList(Long originalGoalId) {
        // 공동목표의 모든 참가자의 사용자 아이디 리스트 반환
        List<Goal> goalList = goalRepository.findGoalsByOriginalGoalId(originalGoalId);
        List<Long> friendIdList = new ArrayList<>();

        for (Goal goal : goalList) {
            friendIdList.add(goal.getUserId());
        }
        return friendIdList;
    }

    private List<Goal> getEditPendingGoal(Long existingGoalId) {
        return goalRepository.findGoalsByExistingGoalId(existingGoalId);
    }

    private Goal getAuthorizedGoal(Long goalId) {
        Optional <Goal> optionalGoal = getGoalById(goalId);
        if (optionalGoal.isEmpty()) {
            return null;
        }
        Goal goal = optionalGoal.get();

        // TODO 본인의 목표가 맞는지 확인
        return goal;
    }

    private void deactivateAndSave(Goal goal) {
        goal.deactivate();
        goalRepository.save(goal);
    }

    private void acceptAndSave(Goal goal) {
        goal.accept();
        goalRepository.save(goal);
    }

    private void rejectAndSave(Goal goal) {
        goal.reject();
        goalRepository.save(goal);
    }

    private void registerAndSave(Goal goal) {
        goal.register();
        goalRepository.save(goal);
    }

    private void completeAndSave(Goal goal) {
        goal.complete();
        goalRepository.save(goal);
    }

    private void failAndSave(Goal goal) {
        goal.fail();
        goalRepository.save(goal);
    }

    private void completePendAndSave(Goal goal) {
        goal.setProgressStatus("complete-pending");
        goalRepository.save(goal);
    }

    private void completeSubGoalAndSave(Goal goal, SubGoal subGoal) {
        subGoal.setProgressStatus("completed"); // 하위 목표를 달성 처리
        goalRepository.save(goal);
    }

    private void finishPeriodAndSave(Goal goal) {
        goal.setFinishedAt(LocalDateTime.now());
        goal.setRewardCount(goal.getRewardCount() + 1);
        if (goal.isRepetitionRemaining()) { // 반복 주기가 남은 경우
            goal.setProgressStatus("progressing");
            goal.updatePeriodDate();
        }
        goalRepository.save(goal);
    }

    // TODO subGoal - isCompleted / periodGoal - completedAt / repeatedGoal - completedCount, failedCount 필요 없으면 제거
}
