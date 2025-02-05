package com.road_journey.road_journey.auth.dao;

import com.road_journey.road_journey.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByAccountId(String accountId);
}
