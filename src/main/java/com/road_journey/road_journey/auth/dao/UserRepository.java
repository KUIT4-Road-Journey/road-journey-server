package com.road_journey.road_journey.auth.dao;

import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.items.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByAccountId(String accountId);
    Optional<User> findByEmail(String email);
    Optional<User> findByAccountId(String accountId);

    @Query("SELECT u FROM User u WHERE u.accountId LIKE %:accountId%")
    List<User> findUsersByAccountId(String accountId);

    @Query("SELECT u.profileImage FROM User u WHERE u.userId = :userId AND u.role = 'USER'")
    String findProfileImageByUserId(Long userId);
}
