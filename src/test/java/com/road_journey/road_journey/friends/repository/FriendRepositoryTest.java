package com.road_journey.road_journey.friends.repository;

import com.road_journey.road_journey.friends.entity.Friend;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.road_journey.road_journey.friends.dto.FriendStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Rollback
public class FriendRepositoryTest {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private EntityManager entityManager;

    private Long friendId1;
    private Long friendId2;
    private Long userId1;
    private Long userId2;

    @BeforeEach
    public void setUp() {
        userId1 = 1L;
        userId2 = 2L;

        // isFriend 상태 친구 관계
        Friend friend1 = new Friend(userId1, userId2, true, IS_FRIEND.name());
        friend1 = friendRepository.save(friend1);
        friendId1 = friend1.getFriendId();

        // pending 상태 친구 요청
        Friend friend2 = new Friend(userId2, userId1, false, PENDING.name());
        friend2 = friendRepository.save(friend2);
        friendId2 = friend2.getFriendId();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void testFindByUserIdAndFriendUserIdAndStatus() {
        Optional<Friend> result = friendRepository.findByUserIdAndFriendUserIdAndStatus(userId1, userId2, IS_FRIEND.name());

        assertTrue(result.isPresent());
        assertEquals(friendId1, result.get().getFriendId());
        assertEquals(userId1, result.get().getUserId());
        assertEquals(userId2, result.get().getFriendUserId());
        assertEquals(IS_FRIEND.name(), result.get().getStatus());
    }

    @Test
    public void testFindFriendsByUserId() {
        List<Friend> friends = friendRepository.findFriendsByUserId(userId1);

        assertFalse(friends.isEmpty());
        assertEquals(1, friends.size());
        assertEquals(friendId1, friends.get(0).getFriendId());
    }

    @Test
    public void testFindFriendsByFriendUserId() {
        List<Friend> friends = friendRepository.findPendingFriendRequestsByFriendUserId(userId1);

        assertFalse(friends.isEmpty());
        assertEquals(1, friends.size());
        assertEquals(friendId2, friends.get(0).getFriendId());
    }

    @Test
    public void testFindFriendsByFriendUserIdWithIsFriendStatus() {
        Optional<Friend> result = friendRepository.findFriendsByFriendUserId(userId1);
        Optional<Friend> result2 = friendRepository.findFriendsByFriendUserId(userId2);

        assertFalse(result.isPresent());
        assertTrue(result2.isPresent());
    }

    @Test
    public void testFindFriendByUserIdAndFriendUserId() {
        Optional<Friend> result = friendRepository.findFriendByUserIdAndFriendUserId(userId1, userId2);

        assertTrue(result.isPresent());
        assertEquals(friendId1, result.get().getFriendId());
        assertEquals(userId1, result.get().getUserId());
        assertEquals(userId2, result.get().getFriendUserId());
    }
}