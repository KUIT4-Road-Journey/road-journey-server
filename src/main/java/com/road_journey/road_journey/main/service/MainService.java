package com.road_journey.road_journey.main.service;

import com.road_journey.road_journey.goals.response.BaseResponse;
import com.road_journey.road_journey.goals.response.ResponseStatus;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    public ResponseStatus getMain(Long userId) {
        // TODO 본인이거나, 친구가 아닌 userId이면 바로 리턴

        // TODO 사용자가 장착 중인 아이템들의 리스트 반환

        return new BaseResponse<>("itemList");
    }
}
