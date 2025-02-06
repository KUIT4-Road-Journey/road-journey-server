package com.road_journey.road_journey.friends.dto;


import com.road_journey.road_journey.friends.entity.Friend;
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
    private Long friendUserId;
    private Boolean isLike;
    private String status;
    private LocalDateTime createdAt;

    public FriendDTO(Friend friend) {
        this.friendId = friend.getFriendId();
        this.userId = friend.getUserId();
        this.friendUserId = friend.getFriendUserId();
        this.isLike = friend.getIsLike();
        this.status = friend.getStatus();
        this.createdAt = friend.getCreatedAt();
    }
}
