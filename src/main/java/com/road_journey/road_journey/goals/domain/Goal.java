package com.road_journey.road_journey.goals.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId; // 목표아이디

    @Column
    private Long userId; // 사용자아이디

    @Column
    private Long originalGoalId; // 원본목표아이디 (공동목표를 생성한 사용자의 목표 아이디)

    @Column
    private Long existingGoalId; // 기존목표아이디 (수정 대기 중인 목표의 원본 목표, 수정 대기 중이 아니라면 null)

    @Column
    private String title; // 목표이름

    @Column
    private int difficulty; // 난이도

    @Column
    private String description; // 상세내용

    @Column
    private String category; // 상세내용

    @Column
    private boolean isSharedGoal; // 공동목표여부

    @Column
    private String sharedGoalType; // 공동목표유형

    @Column
    private boolean isPublic; // 친구공개여부

    @Column
    private String subGoalType; // 하위목표카테고리

    @Column
    private int rewardCount; // 보상 획득 횟수

    @Column
    private String sharedStatus; // 공동상태

    @Column
    private String progressStatus; // 진행상태

    @Column
    private LocalDateTime finishedAt; // 완료일

    @Column
    private String status; // 상태

    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column()
    private LocalDateTime updatedAt; // 수정일


    @OneToOne(mappedBy = "goal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PeriodGoal periodGoal;

    @OneToOne(mappedBy = "goal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RepeatedGoal repeatedGoal;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubGoal> subGoalList = new ArrayList<>();

    public boolean isIncludedInGoalList() {
        if (!isPeriodStarted()) { // 아직 시작일이 되지 않은 경우
            System.out.println("시작일");
            return false;
        }
        if (isRewarded()) { // 이미 보상을 획득한 경우
            System.out.println("보상 획득 여부");
            return false;
        }
        if (isPendingForEdit()) { // 수정 승인 대기 중인 경우
            System.out.println("수정 승인 대기 중");
            return false;
        }
        if (!isMadeByUser()) { // 자신이 생성한 목표가 아니고
            // 공동 목표 승인을 대기 중이거나 거절한 경우
            System.out.println("공동 목표 승인 대기/거절");
            return !sharedStatus.equals("pending") && !sharedStatus.equals("rejected");
        }
        System.out.println("ok"); // TODO 출력문 나중에 없애기
        return true;
    }

    public boolean isIncludedInArchiveList(String finishType) {
        if (rewardCount == 0) {
            return false;
        }
        return finishType.equals("all") || finishType.equals(progressStatus);
    }

    public boolean isPending() {
        return sharedStatus.equals("pending");
    }

    public boolean isPendingForEdit() {
        return existingGoalId != null;
    }

    public boolean isPendingForCreation() {
        return isPending() && !isPendingForEdit();
    }

    public boolean isStarted() {
        return !periodGoal.getStartAt().isAfter(LocalDate.now()); // 시작한 목표인지 확인
    }

    public boolean isRepeated() {
        return category.equals("repeated");
    }

    public boolean isPeriodStarted() {
        return !periodGoal.getPeriodStartAt().isAfter(LocalDate.now()); // 시작한 목표인지 확인
    }

    public boolean isMadeByUser() {
        return Objects.equals(goalId, originalGoalId); // 사용자 본인이 생성한 목표인지 확인
    }

    public boolean isRewarded() { // 해당 주기의 보상을 이미 받았는지 확인
        if (rewardCount == 0) { // 보상 횟수가 0인 경우
            return false; // 보상 받은 적 없음
        }
        if (category.equals("repeated")) { // 반복 목표인 경우
            return rewardCount == repeatedGoal.getRepeatedCount(); // 보상 횟수와 반복 횟수가 같으면 보상 이미 받은 것으로 처리
        }
        return true;
    }

    public int getProgress() {
        if (subGoalList.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (SubGoal subGoal : subGoalList) {
            if (subGoal.getProgressStatus().equals("completed")) {
                count ++;
            }
        }
        return (int)(count / (double)(subGoalList.size() + 1) * 100);
    }

    public void accept() {
        setSharedStatus("accepted");
    }

    public void reject() {
        setSharedStatus("rejected");
    }

    public void register() {
        setSharedStatus("registered");
    }

    public int getDuration() {
        return (int) ChronoUnit.DAYS.between(periodGoal.getPeriodStartAt(), periodGoal.getPeriodExpireAt());
    }

    public double getCategoryCoefficient() {
        return switch (category) {
            case "repeated" -> 1.0;
            case "short-term" -> 1.2;
            case "long-term" -> 1.5;
            default -> 0;
        };
    }

    public int getGold(boolean isReward) {
        double gold = difficulty * getDuration() * getCategoryCoefficient() * 500;
        if (!isReward) {
            gold /= -5;
            return (int) gold;
        }
        if (!subGoalType.equals("normal")) {
            gold /= (subGoalList.size() + 1);
        }
        return (int) gold;
    }

    public int getGrowthPoint(boolean isReward) {
        return (int) ((double)getGold(isReward) / 50);
    }

    public boolean isCompletable() {
        if (!sharedStatus.equals("registered")) {
            System.out.println("등록되지 않은 목표");
            return false;
        }
        if (progressStatus.equals("failed") || progressStatus.equals("completed")) {
            System.out.println("이미 달성 완료/실패 처리된 목표");
            return false;
        }
        if (isRewarded()) {
            System.out.println("이미 보상을 수령한 목표");
            return false;
        }
        for (SubGoal subGoal : subGoalList) {
            if (!subGoal.getProgressStatus().equals("completed")) {
                System.out.println("하위 목표를 모두 완료하지 않음");
                return false;
            }
        }
        return true;
    }

    public boolean isFailable() {
        if (!sharedStatus.equals("registered")) {
            System.out.println("등록되지 않은 목표");
            return false;
        }
        if (!progressStatus.equals("progressing")) {
            return false;
        }
        return !isRewarded();
    }

    public boolean canGetReward() {
        if (isRewarded()) {
            return false;
        }
        return progressStatus.equals("completed") || progressStatus.equals("failed");
    }

    public boolean isSubGoalCompletable(int subGoalIndex) {
        if (!sharedStatus.equals("registered")) {
            System.out.println("등록되지 않은 목표");
            return false;
        }
        if (!subGoalList.get(subGoalIndex).getProgressStatus().equals("progressing")) {
            System.out.println("진행 중인 하위 목표 아님");
            return false;
        }
        if (subGoalType.equals("stepByStep")) {
            for (int i = 0; i < subGoalIndex; i++) {
                if (!subGoalList.get(i).getProgressStatus().equals("completed")) {
                    System.out.println("이전 하위 목표를 먼저 달성해야 함");
                    return false;
                }
            }
        }
        return true;
    }

    public SubGoal getSubGoalById(Long subGoalId) {
        for (SubGoal subGoal : subGoalList) {
            if (Objects.equals(subGoal.getSubGoalId(), subGoalId)) {
                return subGoal;
            }
        }
        return null;
    }

    public void processCompleteOrFail(boolean isCompleted) {
        setProgressStatus(isCompleted ? "completed" : "failed"); // 달성 완료 상태로 변경

        if (category.equals("repeated")) { // 반복 목표인 경우
            repeatedGoal.recordHistory(isCompleted); // repetitionHistory 갱신
            if (repeatedGoal.isRepetitionRemaining()) { // 다음 반복 주기가 있는 경우
                resetSubGoalList(); // 하위 목표 진행도 리셋
            }
        }
    }

    public void updatePeriodDate() {
        periodGoal.updatePeriodDate(repeatedGoal.getRepetitionPeriod()); // 주기 시작일/만료일 갱신
    }

    public void complete() {
        if (!isCompletable()) {
            return;
        }
        processCompleteOrFail(true);
    }

    public void fail() {
        if (!isFailable()) {
            return;
        }
        processCompleteOrFail(false);
    }

    public void resetSubGoalList() {
        for (SubGoal subGoal : subGoalList) {
            subGoal.setProgressStatus("progressing");
        }
    }

    public void deactivate() { // Goal과 해당 Goal에 연결된 Entity들을 비활성화
        setStatus("deactivated");
        periodGoal.deactivate();
        if (repeatedGoal != null) {
            repeatedGoal.deactivate();
        }
        for (SubGoal subGoal : subGoalList) {
            subGoal.deactivate();
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
