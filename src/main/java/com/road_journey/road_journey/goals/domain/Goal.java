package com.road_journey.road_journey.goals.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public boolean isPendingForEdit() {
        return existingGoalId != null;
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

    public boolean isRewarded() {
        if (rewardCount == 0) {
            return false;
        }
        if (category.equals("repeated")) { // 반복 목표인 경우
            return rewardCount == repeatedGoal.getRepeatedCount();
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
