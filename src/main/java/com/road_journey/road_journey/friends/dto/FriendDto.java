package com.road_journey.road_journey.friends.dto;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FriendDto {

    private Long friendId;
    private Long userId;
    private Long friendUserId;
    private boolean isLike;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public FriendDto(Long friendId, Long userId, Long friendUserId, boolean isLike, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.friendId = friendId;
        this.userId = userId;
        this.friendUserId = friendUserId;
        this.isLike = isLike;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static FriendDto fromEntity(Friend friend) {
        return new FriendDto(
                friend.getFriendId(),
                friend.getUserId(),
                friend.getFriendUserId(),
                friend.isLike(),
                friend.getStatus(),
                friend.getCreatedAt(),
                friend.getUpdatedAt()
        );
    }
}
