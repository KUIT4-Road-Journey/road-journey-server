package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.friends.dto.FriendDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
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
@Transactional
@Rollback
@TestPropertySource(locations = "classpath:application-test.properties")
public class FriendRequestServiceTest {

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId1;
    private Long userId2;
    private Long userId3;

    @BeforeEach
    public void setUp() {
        User user1 = new User("user1", "password1", "user1@test.com", "User One", 0L, "active");
        User user2 = new User("user2", "password2", "user2@test.com", "User Two", 0L, "active");
        User user3 = new User("user3", "password3", "user3@test.com", "User Three", 0L, "active");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        userId1 = user1.getUserId();
        userId2 = user2.getUserId();
        userId3 = user3.getUserId();
    }

    @Test
    public void 친구요청_성공() {
        Friend friendRequest = friendRequestService.sendFriendRequest(userId1, userId2);

        assertNotNull(friendRequest);
        assertEquals(userId1, friendRequest.getUserId());
        assertEquals(userId2, friendRequest.getFriendUserId());
        assertEquals(PENDING.name(), friendRequest.getStatus());
    }

    @Test
    public void 중복친구요청_예외발생() {
        friendRequestService.sendFriendRequest(userId1, userId2);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            friendRequestService.sendFriendRequest(userId1, userId2);
        });

        assertEquals("Friend request already pending.", exception.getMessage());
    }

    @Test
    public void 받은친구요청_조회_성공() {
        friendRequestService.sendFriendRequest(userId2, userId1);

        List<FriendDTO> requests = friendRequestService.getFriendRequests(userId1);

        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(userId2, requests.get(0).getUserId());
        assertEquals("user2", requests.get(0).getAccountId());
        assertEquals(PENDING.name(), requests.get(0).getFriendStatus());
    }

    @Test
    public void 친구요청_수락_성공() {
        Friend friendRequest = friendRequestService.sendFriendRequest(userId2, userId1);


        friendRequestService.acceptFriendRequest(userId1, friendRequest.getFriendId());
        Friend acceptedRequest = friendRepository.findById(friendRequest.getFriendId())
                .orElseThrow(() -> new IllegalStateException("Friend request not found."));
        assertEquals(IS_FRIEND.name(), acceptedRequest.getStatus());


        Optional<Friend> reverseRelation = friendRepository.findByUserIdAndFriendUserIdAndStatus(userId1, userId2, IS_FRIEND.name());
        assertTrue(reverseRelation.isPresent());
    }

    @Test
    public void 친구요청_거절_성공() {
        Friend friendRequest = friendRequestService.sendFriendRequest(userId2, userId1);
        friendRequestService.rejectFriendRequest(userId1, friendRequest.getFriendId());

        Friend rejectedRequest = friendRepository.findById(friendRequest.getFriendId())
                .orElseThrow(() -> new IllegalStateException("Friend request not found."));

        assertEquals(DELETED.name(), rejectedRequest.getStatus());
    }

    @Test
    public void 친구요청_수락_권한없음_예외발생() {
        Friend friendRequest = friendRequestService.sendFriendRequest(userId2, userId1);

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            friendRequestService.acceptFriendRequest(userId3, friendRequest.getFriendId());
        });
        assertEquals("Unauthorized action.", exception1.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            friendRequestService.acceptFriendRequest(userId2, friendRequest.getFriendId());
        });
        assertEquals("Unauthorized action.", exception2.getMessage());
    }
}