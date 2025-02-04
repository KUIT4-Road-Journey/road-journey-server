package com.road_journey.road_journey.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")  // 기존 테이블 명이 'user'라면 'users'로 변경
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String email;

    private String nickname;
    private int gold;  // 사용자 골드

    @Column(nullable = false)
    private String role;
}
