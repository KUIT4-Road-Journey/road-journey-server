package com.road_journey.road_journey.friends.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.friends.dto.FriendMessageDTO;
import com.road_journey.road_journey.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendMessageService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<FriendMessageDTO> getFriendMessages(Long userId) {
        return notificationRepository.findByUserIdAndCategoryInAndStatus(
                userId, Arrays.asList("FRIEND_LIKE", "SHARED_GOAL"), "active").stream().map(notification -> {
            String imageUrl = null;
            if ("FRIEND_LIKE".equals(notification.getCategory()) && notification.getRelatedId() != null) {
                imageUrl = userRepository.findProfileImageByUserId(notification.getRelatedId());
            }
            return new FriendMessageDTO(
                    notification.getNotificationId(),
                    notification.getMessage(),
                    notification.getCreatedAt().toString(),
                    notification.getCategory(),
                    imageUrl,
                    notification.getRelatedId()
            );
        }).collect(Collectors.toList());
    }

}
