package com.road_journey.road_journey.friends.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendMessageService { //todo goal 다 완성되면 해야할 듯
//
//    private final NotificationRepository notificationRepository;
//    private final FriendRepository friendRepository;
//    private final UserRepository userRepository;
//    private final GoalService goalService;
//
//    public List<FriendMessageDTO> getFriendMessages(Long userId) {
//        List<String> categories = List.of("sharedGoal", "like");
//
//        return notificationRepository.findFriendMessages(userId, categories).stream()
//                .map(notification -> {
//                    Object detail = null;
//                    String profilePicUrl = null;
//
//                    if ("sharedGoal".equals(notification.getCategory())) {
//                        detail = fetchGoalDetail(notification.getRelatedId());   // 공동 목표 정보 가져오기
//                        Long goalCreatorUserId = getUserIdByGoalId(notification.getRelatedId());
//                        profilePicUrl = getUserProfileImage(goalCreatorUserId);  // 공동 목표 생성자의 프로필 이미지 가져오기
//                    } else if ("like".equals(notification.getCategory())) {
//                        detail = getFriendUserId(notification.getRelatedId());  // 좋아요 누른 UserId 가져오기
//                        profilePicUrl = getUserProfileImage((Long) detail);     // 친구의 프로필 이미지 가져오기
//                    }
//
//                    return new FriendMessageDTO(
//                            notification.getNotificationId(),
//                            profilePicUrl,
//                            notification.getMessage(),
//                            notification.getCreatedAt().toString(),
//                            notification.getCategory(),
//                            detail
//                    );
//                })
//                .collect(Collectors.toList());
//    }
//
//    private SharedGoalDetailDTO fetchGoalDetail(Long goalId) {
//        GoalResponseDto goalResponse = goalService.getGoalResponseByGoalId(goalId);  // GoalService 사용
//
//        return new SharedGoalDetailDTO(
//                goalResponse.getTitle(),
//                goalResponse.getDescription(),
//                goalResponse.getDateInfo().getStartAt().toString(),
//                goalResponse.getDateInfo().getExpireAt().toString(),
//                goalResponse.getDifficulty(),
//                calculateRemainingDays(goalResponse.getDateInfo().getExpireAt().toString()),
//                goalResponse.getSubGoalList().stream()
//                        .map(subGoal -> new StepDTO(
//                                subGoal.getIndex(),
//                                subGoal.getDescription(),
//                                "completed".equals(subGoal.getProgressStatus())
//                        ))
//                        .collect(Collectors.toList())
//        );
//    }
//
//    private Long getUserIdByGoalId(Long goalId) {
//        GoalResponseDto goalResponse = goalService.getGoalResponseByGoalId(goalId);
//        return goalResponse.getUserId();
//    }
//
//    private String getUserProfileImage(Long userId) {
//        Optional<User> user = userRepository.findById(userId);
//        return user.map(User::getProfileImage).orElse("https://example.com/default-profile.jpg");
//    }
//
//    private Long getFriendUserId(Long relatedId) {
//        Optional<Friend> friend = friendRepository.findById(relatedId);
//        return friend.map(Friend::getUserId).orElse(null);
//    }
//
//    private int calculateRemainingDays(String expirationDate) {
//        LocalDate expiration = LocalDate.parse(expirationDate);
//        return (int) ChronoUnit.DAYS.between(LocalDate.now(), expiration);
//    }
}
