package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.friends.dto.FriendStatus;
import com.road_journey.road_journey.friends.dto.FriendUserDTO;
import com.road_journey.road_journey.auth.UserRepository;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.dto.NotificationCategory;
import com.road_journey.road_journey.notifications.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.road_journey.road_journey.friends.dto.FriendStatus.*;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    //친구 요청 보내기
    @Transactional
    public Friend sendFriendRequest(Long userId, Long friendUserId) {
        friendRepository.findByUserIdAndFriendUserIdAndStatus(userId, friendUserId, PENDING.name())
                .ifPresent(friend -> { throw new IllegalStateException("Friend request already pending."); });

        Friend newFriend = new Friend(userId, friendUserId, false, PENDING.name());
        friendRepository.save(newFriend);

        return newFriend;
    }

    //친구 요청 목록 조회
    public List<FriendUserDTO> getFriendRequests(Long userId) {
        List<Friend> pendingRequests = friendRepository.findFriendsByUserId(userId).stream()
                .filter(friend -> PENDING.name().equals(friend.getStatus()))
                .collect(Collectors.toList());

        if (pendingRequests.isEmpty()) {
            return Collections.emptyList();
        }

        return pendingRequests.stream()
                .map(friend -> {
                    User user = userRepository.findById(friend.getUserId())
                            .orElseThrow(() -> new IllegalStateException("User not found"));
                    return new FriendUserDTO(user, PENDING.name(), friend.getUserId());
                })
                .collect(Collectors.toList());
    }

    //친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Long userId, Long friendId) {
        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new IllegalStateException("Friend request not found."));

        if (!friendRequest.getFriendUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized action.");
        }

        friendRequest.setStatus(FriendStatus.IS_FRIEND.name());
        friendRepository.save(friendRequest);

        //반대 방향 친구 관계 추가 (userId → friendUserId)
        Friend newFriendRelation = new Friend(friendRequest.getFriendUserId(), friendRequest.getUserId(), false, FriendStatus.IS_FRIEND.name());
        friendRepository.save(newFriendRelation);

//        notificationService.deactivateNotification(friendId, NotificationCategory.FRIEND.name());
    }

    //친구 요청 거절
    @Transactional
    public void rejectFriendRequest(Long userId, Long friendId) {
        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new IllegalStateException("Friend request not found."));

        if (!friendRequest.getFriendUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized action.");
        }

        // 요청 삭제
        friendRequest.setStatus(FriendStatus.IS_NOT_FRIEND.name());
        friendRepository.save(friendRequest);

//        notificationService.deactivateNotification(friendId, NotificationCategory.FRIEND.name());
    }
}