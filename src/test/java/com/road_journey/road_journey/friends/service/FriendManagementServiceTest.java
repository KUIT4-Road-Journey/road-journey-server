package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.road_journey.road_journey.friends.dto.FriendStatus.IS_FRIEND;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@TestPropertySource(locations = "classpath:application-test.properties")
public class FriendManagementServiceTest {

    @Autowired
    private FriendManagementService friendManagementService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId1;
    private Long userId2;
    private Long userId3;

    @BeforeEach
    public void setUp() {
        User user1 = new User("user1", "password1", "user1@test.com", "User One", 0L);
        User user2 = new User("user2", "password2", "user2@test.com", "User Two", 0L);
        User user3 = new User("user3", "password3", "user3@test.com", "User Three", 0L);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        userId1 = user1.getUserId();
        userId2 = user2.getUserId();
        userId3 = user3.getUserId();

        friendRepository.save(new Friend(userId1, userId2, true, "IS_FRIEND"));
        friendRepository.save(new Friend(userId1, userId3, false, "IS_FRIEND"));
    }

    @Test
    public void 친구목록_조회_성공() {
        List<FriendListDTO> friends = friendManagementService.getFriends(userId1, "alphabetical");

        assertFalse(friends.isEmpty());
        assertEquals(2, friends.size());
        assertEquals("User Three", friends.get(0).getNickname());
        assertEquals("User Two", friends.get(1).getNickname());
    }

    @Test
    public void 친구목록_정렬_최근접속순_성공() {
        List<FriendListDTO> friends = friendManagementService.getFriends(userId1, "lastLogin");

        assertFalse(friends.isEmpty());
        assertEquals(2, friends.size());
        assertTrue(friends.get(0).getLastLoginTime() >= friends.get(1).getLastLoginTime());
    }

    @Test
    public void 친구목록_정렬_목표수순_성공() {
        List<FriendListDTO> friends = friendManagementService.getFriends(userId1, "goals");

        assertFalse(friends.isEmpty());
        assertEquals(2, friends.size());
        assertTrue(friends.get(0).getAchievementCount() >= friends.get(1).getAchievementCount());
    }

    @Test
    public void 친구좋아요_상태변경_성공() {
        Friend friend = friendRepository.findByUserIdAndFriendUserIdAndStatus(userId1, userId2, "IS_FRIEND")
                .orElseThrow(() -> new IllegalStateException("Friend not found."));

        assertTrue(friendManagementService.getFriendLikeStatus(userId1, userId2));


        UpdateResponseDTO response = friendManagementService.updateFriendLike(friend.getFriendId(), false);
        assertEquals("success", response.getStatus());

        Friend updatedFriend = friendRepository.findById(friend.getFriendId())
                .orElseThrow(() -> new IllegalStateException("Friend not found."));
        assertFalse(updatedFriend.getIsLike());
        assertFalse(friendManagementService.getFriendLikeStatus(userId1, userId2));
    }

    @Test
    public void 친구좋아요_상태변경_친구없음() {
        UpdateResponseDTO response = friendManagementService.updateFriendLike(999L, true);

        assertEquals("failed", response.getStatus());
        assertEquals("Friend not found.", response.getMessage());
    }

    @Test
    public void 친구좋아요_상태_반환_성공() {
        Friend friend = friendRepository.findByUserIdAndFriendUserIdAndStatus(userId1, userId2, IS_FRIEND.name())
                .orElseThrow(() -> new IllegalStateException("Friend not found."));
        friend.setIsLike(true);
        friendRepository.save(friend);

        assertTrue(friendManagementService.getFriendLikeStatus(userId1, userId2));
    }

    @Test
    public void 친구좋아요_상태_반환_친구없음() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            friendManagementService.getFriendLikeStatus(userId1, 999L);
        });

        assertEquals("Friend relationship not found.", exception.getMessage());
    }
}