package com.road_journey.road_journey.goals.util;

import com.road_journey.road_journey.goals.domain.Goal;
import com.road_journey.road_journey.goals.dto.AddGoalRequestDto;

import java.util.List;

public class GoalUtil {
    public GoalUtil() {

    }

    public static String getInitialSharedStatus(AddGoalRequestDto addGoalRequest, Long originalGoalId) {
        if (originalGoalId == null) { // 사용자 본인이 생성한 목표
            if (addGoalRequest.isSharedGoal()) { // 공유 목표인 경우
                return "accepted"; // 수락
            }
            return "registered"; // 등록
        }
        // 공동 목표 요청을 받은 경우
        return "pending"; // 대기중
    }

    public static Goal createGoalByRequest(Long userId, AddGoalRequestDto addGoalRequest, Long originalGoalId) {
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
                .sharedStatus(GoalUtil.getInitialSharedStatus(addGoalRequest, originalGoalId))
                .progressStatus("progressing") // TODO 상태값 수정 필요
                .finishedAt(null)
                .status("activated") // TODO 문자열 하드코딩 처리
                .build();
    }

    public static Goal editGoalByRequest(Goal goal, AddGoalRequestDto addGoalRequest, Long originalGoalId) {
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
                .sharedStatus(GoalUtil.getInitialSharedStatus(addGoalRequest, originalGoalId))
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

    public static boolean isAllAccepted(List<Goal> sharedGoalList) {
        for (Goal sharedGoal : sharedGoalList) {
            if (!sharedGoal.getSharedStatus().equals("accepted")){
                return false;
            }
        }
        return true; // 모든 공동 목표들이 수락되어 있는지 확인
    }

    public static boolean isAllCompletePending(List<Goal> sharedGoalList) {
        // 모든 공동 목표가 달성 완료 대기 중인지 확인
        for (Goal sharedGoal : sharedGoalList) {
            if (!sharedGoal.getProgressStatus().equals("complete-pending")) {
                return false;
            }
        }
        return true;
    }
}
