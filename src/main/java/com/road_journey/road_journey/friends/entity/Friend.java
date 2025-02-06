package com.road_journey.road_journey.friends.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@Getter
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long friendId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "friend_user_id", nullable = false)
    private Long friendUserId;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 생성자 (빌더 패턴 없이 사용)
    public Friend(Long userId, Long friendUserId, boolean isLike, String status) {
        this.userId = userId;
        this.friendUserId = friendUserId;
        this.isLike = isLike;
        this.status = status;
    }

    // 엔티티가 저장될 때 자동으로 실행
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 엔티티가 업데이트될 때 자동으로 실행
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // isLike 변경 메서드
    public void updateLikeStatus(boolean isLike) {
        this.isLike = isLike;
    }

    // status 변경 메서드
    public void updateStatus(String status) {
        this.status = status;
    }
}
