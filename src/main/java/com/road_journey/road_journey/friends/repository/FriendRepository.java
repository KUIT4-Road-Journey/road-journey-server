package com.road_journey.road_journey.friends.repository;

import com.road_journey.road_journey.friends.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f FROM Friend f WHERE f.friendId = :friendId AND f.status NOT IN ('DELETED')")
    Optional<Friend> findActiveFriendsByFriendId(Long friendId);

    // 특정 친구 관계 조회 (status 필터링)
    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.friendUserId = :friendUserId AND f.status != 'DELETED'")
    Optional<Friend> findByUserIdAndFriendUserIdAndStatus(Long userId, Long friendUserId, String status);

    // 특정 사용자의 모든 친구 목록 조회
    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.status = 'IS_FRIEND'")
    List<Friend> findFriendsByUserId(Long userId);

    // 특정 사용자의 받은 친구 요청 목록 조회
    @Query("SELECT f FROM Friend f WHERE f.friendUserId = :friendUserId AND f.status = 'PENDING' AND f.status != 'DELETED'")
    List<Friend> findPendingFriendRequestsByFriendUserId(Long friendUserId);

    // 친구 관계 (isFriend) 조회
    @Query("SELECT f FROM Friend f WHERE f.friendUserId = :friendUserId AND f.status = 'IS_FRIEND' AND f.status != 'DELETED'")
    Optional<Friend> findFriendsByFriendUserId(Long friendUserId);

    // 모든 친구 관계 조회 (active, pending 모두 포함)
    @Query("SELECT f FROM Friend f WHERE f.userId = :userId AND f.friendUserId = :friendUserId AND f.status != 'DELETED'")
    Optional<Friend> findFriendByUserIdAndFriendUserId(Long userId, Long friendUserId);
}