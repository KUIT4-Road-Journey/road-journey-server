package com.road_journey.road_journey.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.accountId LIKE %:accountId%")
    List<User> findUsersByAccountId(String accountId);
}
