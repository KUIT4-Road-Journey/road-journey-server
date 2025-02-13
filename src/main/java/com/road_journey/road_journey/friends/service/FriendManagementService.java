package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.friends.dto.FriendListDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.dto.UpdateResponseDTO;
import com.road_journey.road_journey.notifications.entity.Notification;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import com.road_journey.road_journey.notifications.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.road_journey.road_journey.friends.dto.FriendStatus.IS_FRIEND;
import static com.road_journey.road_journey.friends.dto.FriendStatus.IS_NOT_FRIEND;
import static com.road_journey.road_journey.notifications.dto.NotificationCategory.FRIEND_LIKE;
import static com.road_journey.road_journey.notifications.dto.NotificationCategory.NOTIFICATION;

@Service
@RequiredArgsConstructor
public class FriendManagementService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private final NotificationService notificationService;

    //친구 목록 조회
    public List<FriendListDTO> getFriends(Long userId, String sortBy) {
        List<Friend> friends = friendRepository.findFriendsByUserId(userId);

        if (friends.isEmpty()) {
            return Collections.emptyList();
        }

        List<FriendListDTO> friendList = friends.stream()
                .map(friend -> {
                    User user = userRepository.findById(friend.getFriendUserId())
                            .orElse(null);

                    long lastLoginMillis = Optional.ofNullable(user)
                            .map(User::getLastLoginTime)
                            .map(time -> time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .orElse(0L);

                    return new FriendListDTO(user, lastLoginMillis, getAchievementCount(user.getUserId()));
                })
                .collect(Collectors.toList());

        return sortFriendsByCategory(friendList, sortBy);
    }

    private List<FriendListDTO> sortFriendsByCategory(List<FriendListDTO> friends, String sortBy) {
        switch (sortBy) {
            case "lastLogin":
                friends.sort(Comparator.comparing(FriendListDTO::getLastLoginTime, Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case "goals":
                friends.sort(Comparator.comparingInt(FriendListDTO::getAchievementCount).reversed());
                break;
            case "alphabetical":
            default:
                friends.sort(Comparator.comparing(FriendListDTO::getNickname));
                break;
        }
        return friends;
    }

    private int getAchievementCount(Long userId) {
        // todo : 목표 테이블에서 해당 유저의 달성 목표 수 조회
        return userId.intValue();
    }


    @Transactional
    public Map<String, String> validateFriendshipAndDeactivateNotification(Long userId, Long friendId, Long notificationId) {
        Map<String, String> result = new HashMap<>();

        Optional<Friend> friend = friendRepository.findByUserIdAndFriendUserIdAndStatus(userId, friendId, IS_FRIEND.name());

        if (friend.isPresent()) {
            result.put("friendStatus", IS_FRIEND.name());
            result.put("activeStatus", friend.get().getIsLike() ? "active" : "not active"); //todo 접근 여부 설정

            if (notificationId != null) {
                notificationService.deleteNotification(notificationId);
            }
        } else {
            result.put("friendStatus", IS_NOT_FRIEND.name());
            result.put("activeStatus", "N/A");
        }

        return result;
    }

    //친구 좋아요 상태 변경
    @Transactional
    public UpdateResponseDTO updateFriendLike(Long friendId, Boolean isLike) {
        Optional<Friend> friendOptional = friendRepository.findActiveFriendsByFriendId(friendId);

        if (friendOptional.isEmpty()) {
            return new UpdateResponseDTO("failed", "Friend not found.");
        }

        Friend friend = friendOptional.get();
        friend.setIsLike(isLike);
        friendRepository.save(friend);

        if (isLike) {
            String nickname = userRepository.findNicknameByUserId(friend.getUserId());
            String message = String.format("%s님이 내 프로필에 좋아요를 눌렀어요!", nickname);

            notificationRepository.save(new Notification(friend.getFriendUserId(), NOTIFICATION.name(), friend.getUserId(), message));
            notificationRepository.save(new Notification(friend.getFriendUserId(), FRIEND_LIKE.name(), friend.getUserId(), message));
        }

        return new UpdateResponseDTO("success", "Friend like status updated.");
    }

    // 친구의 좋아요 상태 반환
    public Boolean getFriendLikeStatus(Long userId, Long friendUserId) {
        Optional<Friend> friendOptional = friendRepository.findByUserIdAndFriendUserIdAndStatus(userId, friendUserId, IS_FRIEND.name());

        if (friendOptional.isEmpty()) {
            throw new IllegalStateException("Friend relationship not found.");
        }

        return friendOptional.get().getIsLike();
    }
}
