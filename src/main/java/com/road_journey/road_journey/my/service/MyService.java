package com.road_journey.road_journey.my.service;

import com.road_journey.road_journey.auth.dao.UserRepository;
import com.road_journey.road_journey.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyService {

    private final UserRepository userRepository;

    public Map<String, Object> getProfile(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");

        Map<String, String> data = new LinkedHashMap<>();
        data.put("accountId", user.getAccountId());
        data.put("email", user.getEmail());
        data.put("nickname", user.getNickname());
        data.put("profileImage", user.getProfileImage());
        data.put("statusMessage", user.getStatusMessage());

        response.put("data", data);

        return response;

    }
}
