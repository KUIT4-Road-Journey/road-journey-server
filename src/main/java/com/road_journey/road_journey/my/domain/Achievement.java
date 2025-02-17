package com.road_journey.road_journey.my.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "achievement")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Long id;

    @Column(name = "achievement_name", length = 100, nullable = false)
    private String achievementName;

    @Column(name = "description", length = 200, nullable = false)
    private String description;

    @Column(name = "growth_point", nullable = false)
    private int growthPoint; // 성장 포인트 보상

    @Column(name = "gold", nullable = false)
    private int gold; // 골드 보상

    @Column(name = "category", length = 100)
    private String category; // 목표(goal), 캐릭터(character), 아이템(item), 기타(etc)

    @Column(name = "status", length = 50, nullable = false)
    private String status; // ACTIVE or INACTIVE

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Achievement(String achievementName, String description, int growthPoint, int gold, String category, String status) {
        this.achievementName = achievementName;
        this.description = description;
        this.growthPoint = growthPoint;
        this.gold = gold;
        this.category = category;
        this.status = status;
    }
}
