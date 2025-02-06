package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.User;
import com.road_journey.road_journey.auth.UserDTO;
import com.road_journey.road_journey.friends.entity.Friend;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.notifications.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRepository friendRepository;
    private final NotificationService notificationService;

    //친구 요청 보내기
    @Transactional
    public Friend sendFriendRequest(Long userId, Long friendUserId) {
        // 중복 요청 방지
        friendRepository.findByUserIdAndFriendUserIdAndStatus(userId, friendUserId, "대기중")
                .ifPresent(friend -> { throw new IllegalStateException("Friend request already pending."); });

        Friend newFriend = new Friend(userId, friendUserId, false, "대기중");
        friendRepository.save(newFriend);

        // Notification 추가
        notificationService.createNotification(friendUserId, "친구요청", newFriend.getFriendId());

        return newFriend;
    }

    //친구 요청 목록 조회
    public List<UserDTO> getFriendRequests(Long userId) {
        List<Friend> pendingRequests = friendRepository.findFriendsByUserId(userId).stream()
                .filter(friend -> "대기중".equals(friend.getStatus()))
                .collect(Collectors.toList());

        return pendingRequests.stream()
                .map(friend -> {
                    User user = userRepository.findById(friend.getUserId())
                            .orElseThrow(() -> new UserNotFoundException("User not found"));
                    return new UserDTO(user, 1); // 1: 대기중
                })
                .collect(Collectors.toList());
    }

    //친구 요청 수락
    @Transactional
    public void acceptFriendRequest(Long userId, Long friendId) {
        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendNotFoundException("Friend request not found."));

        if (!friendRequest.getFriendUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized action.");
        }

        //기존 요청을 "active"로 변경
        friendRequest.setStatus("active");
        friendRepository.save(friendRequest);

        //반대 방향 친구 관계 추가 (userId → friendUserId)
        Friend newFriendRelation = new Friend(friendRequest.getFriendUserId(), friendRequest.getUserId(), false, "active");
        friendRepository.save(newFriendRelation);
    }

    //친구 요청 거절
    @Transactional
    public void rejectFriendRequest(Long userId, Long friendId) {
        Friend friendRequest = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendNotFoundException("Friend request not found."));

        if (!friendRequest.getFriendUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized action.");
        }

        // 요청 삭제
        friendRepository.delete(friendRequest);
    }
}