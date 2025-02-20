package com.road_journey.road_journey.main.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import com.road_journey.road_journey.friends.repository.FriendRepository;
import com.road_journey.road_journey.goals.response.BaseErrorResponse;
import com.road_journey.road_journey.goals.response.BaseResponse;
import com.road_journey.road_journey.goals.response.ResponseStatus;
import com.road_journey.road_journey.goals.response.ResponseStatusType;
import com.road_journey.road_journey.goals.util.UserUtil;
import com.road_journey.road_journey.items.dto.UserItemInfoDto;
import com.road_journey.road_journey.items.entity.UserItem;
import com.road_journey.road_journey.items.repository.UserItemRepository;
import com.road_journey.road_journey.main.dto.MainResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private UserItemRepository userItemRepository;

    public ResponseStatus getMain(Long myUserId, Long userId) {
        if (!UserUtil.isMeOrMyFriend(myUserId, userId, friendRepository.findFriendsByUserId(myUserId))) {
            return new BaseErrorResponse(ResponseStatusType.BAD_REQUEST); // 본인이거나, 친구가 아닌 userId이면 바로 리턴
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));
        List<UserItemInfoDto> selectedUserItemList = userItemRepository.findUserItemsWithItemDetails(userId);
        return new BaseResponse<>(new MainResponseDto(user.getNickname(), selectedUserItemList));
    }
}
