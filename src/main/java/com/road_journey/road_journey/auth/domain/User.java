package com.road_journey.road_journey.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ACCOUNT_ID", nullable = false, unique = true)
    private String accountId;

    @Column(name = "PASSWORD", nullable = false)
    private String accountPassword;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String nickname;
    private String profileImage;
    private String statusMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private RoleType role;

}
