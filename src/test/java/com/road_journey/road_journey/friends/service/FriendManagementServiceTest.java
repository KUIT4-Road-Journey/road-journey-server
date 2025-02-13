package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.road_journey.road_journey.friends.dto.FriendStatus.IS_FRIEND;
import static com.road_journey.road_journey.notifications.dto.NotificationCategory.*;
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

    @Autowired
    private NotificationRepository notificationRepository;

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

        LocalDateTime firstLoginTime = parseLocalDateTime(friends.get(0).getLastLoginTime());
        LocalDateTime secondLoginTime = parseLocalDateTime(friends.get(1).getLastLoginTime());

        assertNotNull(firstLoginTime);
        assertNotNull(secondLoginTime);

        assertTrue(firstLoginTime.isAfter(secondLoginTime) || firstLoginTime.isEqual(secondLoginTime));
    }

    private LocalDateTime parseLocalDateTime(String dateTime) {
        if ("N/A".equals(dateTime) || dateTime == null) {
            return LocalDateTime.MIN;  // 변환 실패 시 최소값 반환
        }
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return LocalDateTime.MIN;  // 변환 실패 시 최소값 반환
        }
    }

    @Test
    public void 친구목록_정렬_목표수순_성공() {
        List<FriendListDTO> friends = friendManagementService.getFriends(userId1, "goals");

        assertFalse(friends.isEmpty());
        assertEquals(2, friends.size());
        assertTrue(friends.get(0).getAchievementCount() >= friends.get(1).getAchievementCount());
    }

    @Test
    public void 친구좋아요_상태변경_및_알림_생성_성공() {
        Friend friend = friendRepository.findByUserIdAndFriendUserIdAndStatus(userId1, userId2, "IS_FRIEND")
                .orElseThrow(() -> new IllegalStateException("Friend not found."));

        assertTrue(friendManagementService.getFriendLikeStatus(userId1, userId2));

        // Act: 좋아요 상태를 false로 변경
        UpdateResponseDTO response = friendManagementService.updateFriendLike(friend.getFriendId(), false);

        // Assert: 상태 변경 확인
        assertEquals("success", response.getStatus());
        Friend updatedFriend = friendRepository.findById(friend.getFriendId())
                .orElseThrow(() -> new IllegalStateException("Friend not found."));
        assertFalse(updatedFriend.getIsLike());
        assertFalse(friendManagementService.getFriendLikeStatus(userId1, userId2));

        // Act: 좋아요 상태를 true로 변경
        response = friendManagementService.updateFriendLike(friend.getFriendId(), true);

        // Assert: 상태 변경 확인
        assertEquals("success", response.getStatus());
        updatedFriend = friendRepository.findById(friend.getFriendId())
                .orElseThrow(() -> new IllegalStateException("Friend not found."));
        assertTrue(updatedFriend.getIsLike());

        // Verify: 두 개의 Notification 생성 여부 확인
        List<Notification> notifications = notificationRepository.findByUserIdAndStatus(userId2, "active");
        assertEquals(2, notifications.size());

        // 첫 번째 Notification (NOTIFICATION)
        Notification notification1 = notifications.stream()
                .filter(n -> n.getCategory().equals(NOTIFICATION.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("NOTIFICATION category not found."));
        assertTrue(notification1.getMessage().contains("님이 내 프로필에 좋아요를 눌렀어요!"));

        // 두 번째 Notification (FRIEND_LIKE)
        Notification notification2 = notifications.stream()
                .filter(n -> n.getCategory().equals(FRIEND_LIKE.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("FRIEND_LIKE category not found."));
        assertTrue(notification2.getMessage().contains("님이 내 프로필에 좋아요를 눌렀어요!"));
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