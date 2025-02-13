package com.road_journey.road_journey.my.domain;

import com.road_journey.road_journey.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_achievement")
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_achievement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id2", referencedColumnName = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "is_reward_accepted", nullable = false)
    private boolean isRewardAccepted;

    @Column(name = "progress", nullable = false)
    private int progress = 0;

    @Column(name = "achievement_completion_at")
    private LocalDateTime achievementCompletionAt;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UserAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
        this.isRewardAccepted = false;
        this.progress = 0;
        this.achievementCompletionAt = null;
        this.status = "ACTIVE";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
