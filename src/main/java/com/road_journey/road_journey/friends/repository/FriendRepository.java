package com.road_journey.road_journey.friends.repository;

import com.road_journey.road_journey.friends.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    // 특정 친구 관계 조회 (status 필터링)
    Optional<Friend> findByUserIdAndFriendUserIdAndStatus(Long userId, Long friendUserId, String status);

    // 특정 사용자의 모든 친구 목록 조회
    @Query("SELECT f FROM Friend f WHERE f.userId = :userId")
    List<Friend> findFriendsByUserId(Long userId);

    // 특정 사용자의 받은 친구 요청 목록 조회
    @Query("SELECT f FROM Friend f WHERE f.friendUserId = :friendUserId")
    List<Friend> findFriendsByFriendUserId(Long friendUserId);

    // 친구 관계 (isFriend) 조회
    @Query("SELECT f FROM Friend f WHERE f.friendUserId = :friendUserId AND f.status = 'isFriend'")
    Optional<Friend> findFriendsByfriendUserId(Long friendUserId);

    // 모든 친구 관계 조회 (active, pending 모두 포함)
    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.friendUserId = :friendUserId")
    Optional<Friend> findFriendByUserIdAndFriendUserId(Long userId, Long friendUserId);
}
