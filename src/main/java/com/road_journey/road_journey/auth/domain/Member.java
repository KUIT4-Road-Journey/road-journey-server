package com.road_journey.road_journey.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "ACCOUNT_ID", nullable = false, unique = true)
    private String accountId;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "NICKNAME", nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private RoleType role;

}
