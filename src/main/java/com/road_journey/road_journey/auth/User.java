package com.road_journey.road_journey.auth;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "account_id", unique = true, nullable = false, length = 50)
    private String accountId;

    @Column(name = "account_pw", nullable = false, length = 200)
    private String accountPw;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime = LocalDateTime.now();

    @Column(name = "profile_image", length = 200)
    private String profileImage;

    @Column(name = "status_message", length = 200)
    private String statusMessage;

    @Column(nullable = false)
    private Long gold = 0L;

    @Column(nullable = false, length = 50)
    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User(String accountId, String accountPw, String email, String nickname, Long gold, String status) {
        this.accountId = accountId;
        this.accountPw = accountPw;
        this.email = email;
        this.nickname = nickname;
        this.gold = gold;
        this.status = status;
    }
}
