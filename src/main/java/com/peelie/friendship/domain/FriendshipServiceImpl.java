package com.peelie.friendship.domain;

import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileInfo;
import com.peelie.profile.domain.ProfileReader;
import com.peelie.user.domain.User;
import com.peelie.user.domain.UserReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipReader friendshipReader;
    private final FriendshipStore friendshipStore;
    private final ProfileReader profileReader;
    private final UserReader userReader;


    //Todo: 스테이지 단계 계산하는거 추가하기

    @Override
    @Transactional
    public FriendshipInfo.FriendDetail createFriendship(Long senderId, Long receiverId) {
        // 보내는 사람 아이디와 받는 사람 아이디를 입력 받는다. - 파라미터
        // 무방향성을 위한 대소 비교를 한다.
        Long a = Math.min(senderId, receiverId);
        Long b = Math.max(senderId, receiverId);

        if(!friendshipReader.existPair(a, b)) {  // 기존의 것과 비교해서 없으면 객체 생성 후 저장한다.
            Friendship initfriendship = new Friendship(a, b);
            friendshipStore.store(initfriendship);
        }
        Friendship friendship = friendshipReader.getByPair(a, b);
        FriendShipStage stage = friendship.getStageFor(senderId);
        Profile profile = profileReader.getProfile(receiverId);

        return new FriendshipInfo.FriendDetail(profile, stage);
    }

    @Override
    public FriendshipInfo.FriendListResponse getFriendList(Long senderId) {
        // 1. 친구 id 목록
        List<Long> friendIds = friendshipReader.findFriendsByUserId(senderId);

        // 2. 프로필 목록 조회
        List<Profile> profiles = profileReader.getProfilesByUserIds(friendIds);

        // 3. 각 프로필에 대해 friendship + stage 계산 후 FriendDetail 생성
        List<FriendshipInfo.FriendDetail> friends = profiles.stream()
                .map(profile -> {
                    Long friendId = profile.getUserId();

                    // (userId, friendId) 쌍으로 Friendship 가져오기
                    Friendship friendship = friendshipReader.getByPair(senderId, friendId);

                    //userId 기준으로 stage 가져오기
                    FriendShipStage stage = friendship.getStageFor(senderId);

                    // stage를 넘겨서 FriendDetail 생성
                    return new FriendshipInfo.FriendDetail(profile, stage);
                })
                .toList();

        return new FriendshipInfo.FriendListResponse(friends);
    }

    @Override
    public FriendshipInfo.FriendDetail getFriendDetail(Long senderId, Long receiverId) {
        Profile profile = profileReader.getProfile(receiverId);

        Friendship friendship = friendshipReader.getByPair(senderId, receiverId);
        FriendShipStage stage = friendship.getStageFor(senderId);

        return new FriendshipInfo.FriendDetail(profile, stage);
    }

    @Override
    public FriendshipInfo.RandomFriendResponse getRandomFriend(Long userId) {
        // 친구 아이디 리스트 조회
        List<Long> friendIds = friendshipReader.findFriendsByUserId(userId);

        if (friendIds.isEmpty()) {
            return new FriendshipInfo.RandomFriendResponse(List.of());
        }

        // 24시간 타이머
        long seed = Objects.hash(userId, LocalDate.now());
        Collections.shuffle(friendIds, new Random(seed));

        // 최대 5명만 추출
        List<Long> randomFiveIds = friendIds.stream()
                .limit(5)
                .toList();

        // 프로필을 한 번에 조회 (n+1 방지)
        List<Profile> profiles = profileReader.getProfilesByUserIds(randomFiveIds);

        List<FriendshipInfo.FriendDetail> items = profiles.stream()
                .map(profile -> {
                    Long friendId = profile.getUserId();

                    // 유저아이디 프렌드아이디 쌍으로 가져오기
                    Friendship friendship = friendshipReader.getByPair(userId, friendId);

                    // 유저아이디 기준으로 교류단계 가져오기
                    FriendShipStage stage = friendship.getStageFor(userId);

                    // 교류단계까지 넣어서 FriendDetail 생성
                    return new FriendshipInfo.FriendDetail(profile, stage);
                })
                .toList();

        return new FriendshipInfo.RandomFriendResponse(items);
    }
}
