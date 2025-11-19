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
import java.util.*;

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

        // 기존의 것과 비교해서 없으면 객체 생성 후 저장한다.
        if(!friendshipReader.existPair(senderId, receiverId)) {
            Friendship initfriendship = new Friendship(senderId, receiverId);
            friendshipStore.store(initfriendship);
        }

        Friendship friendship = friendshipReader.getByPair(senderId, receiverId);
        FriendShipStage stage = friendship.getStageFor(senderId);
        Profile profile = profileReader.getProfileByUserId(receiverId);

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

                    // (senderId, friendId) 쌍으로 Friendship 가져오기
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

        // null 또는 친구 없음 처리
        if (friendIds == null || friendIds.isEmpty()) {
            return new FriendshipInfo.RandomFriendResponse(List.of());
        }

        // 24시간 타이머 시드 + 셔플을 위해 가변 리스트로 복사
        List<Long> shuffledFriendIds = new ArrayList<>(friendIds);
        long seed = Objects.hash(userId, LocalDate.now());
        Collections.shuffle(shuffledFriendIds, new Random(seed));

        // 최대 5명만 추출
        List<Long> randomFiveIds = shuffledFriendIds.stream()
                .limit(5)
                .toList();

        // 프로필을 한 번에 조회 (N+1 방지)
        List<Profile> profiles = profileReader.getProfilesByUserIds(randomFiveIds);

        List<FriendshipInfo.FriendDetail> items = profiles.stream()
                .map(profile -> {
                    Long friendId = profile.getUserId();
                    Friendship friendship = friendshipReader.getByPair(userId, friendId);
                    FriendShipStage stage = friendship.getStageFor(userId);
                    return new FriendshipInfo.FriendDetail(profile, stage);
                })
                .toList();

        return new FriendshipInfo.RandomFriendResponse(items);
    }
}
