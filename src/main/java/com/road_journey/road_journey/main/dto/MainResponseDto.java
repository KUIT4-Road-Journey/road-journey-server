package com.road_journey.road_journey.main.dto;

import com.road_journey.road_journey.items.entity.UserItem;
import lombok.Getter;

import java.util.List;

@Getter
public class MainResponseDto {
    private final String nickName;
    private final List<UserItem> selectedUserItemList;

    public MainResponseDto(String nickName, List<UserItem> selectedUserItemList) {
        this.nickName = nickName;
        this.selectedUserItemList = selectedUserItemList;
    }
}
