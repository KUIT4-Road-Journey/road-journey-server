package com.road_journey.road_journey.auth.dao;

import com.road_journey.road_journey.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByAccountId(String accountId);
    Optional<User> findByEmail(String email);
    Optional<User> findByAccountId(String accountId);
}
