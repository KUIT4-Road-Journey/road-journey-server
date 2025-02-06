package com.road_journey.road_journey.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String accountId;
    private String nickname;
    private String profileImage;
    private String statusMessage;
    private int friendStatus; // 0: 친구 아님, 1: 대기중, 2: 친구

    public UserDTO(User user, int friendStatus) {
        this.userId = user.getUserId();
        this.accountId = user.getAccountId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.statusMessage = user.getStatusMessage();
        this.friendStatus = friendStatus;
    }
}

