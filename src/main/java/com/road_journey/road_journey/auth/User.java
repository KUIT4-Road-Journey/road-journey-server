package com.road_journey.road_journey.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")  // MySQL 테이블 명과 일치
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 50)
    private String accountId;

    @Column(nullable = false, length = 200)
    private String accountPw;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime lastLoginTime;

    @Column(length = 200)
    private String profileImage;

    @Column(nullable = false, length = 200)
    private String statusMessage;

    @Column(nullable = false)
    private Long gold = 0L;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // @PrePersist: INSERT 시 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastLoginTime = LocalDateTime.now();  // 기본값 설정
    }

    // @PreUpdate: UPDATE 시 자동 설정
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User(String accountId, String accountPw, String email, String nickname, Long gold, String status) {
        this.accountId = accountId;
        this.accountPw = accountPw;
        this.email = email;
        this.nickname = nickname;
        this.gold = gold;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastLoginTime = LocalDateTime.now();
        this.statusMessage = "";
    }
}