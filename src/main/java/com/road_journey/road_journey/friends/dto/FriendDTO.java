package com.road_journey.road_journey.friends.dto;

import com.road_journey.road_journey.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {
    private Long friendId;
    private Long userId;
    private String accountId;
    private String nickname;
    private String profileImage;
    private String statusMessage;
    private String friendStatus;
    private LocalDateTime createdAt;

    public FriendDTO(User user, String friendStatus, Long friendId, LocalDateTime createdAt) {
        this.userId = user.getUserId();
        this.accountId = user.getAccountId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.statusMessage = user.getStatusMessage();
        this.friendStatus = friendStatus;
        this.friendId = friendId;
        this.createdAt = createdAt;
    }
}

