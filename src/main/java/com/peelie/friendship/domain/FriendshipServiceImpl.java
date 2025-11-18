package com.peelie.friendship.domain;

import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileInfo;
import com.peelie.profile.domain.ProfileReader;
import com.peelie.user.domain.User;
import com.peelie.user.domain.UserReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

        // 기존의 것과 비교해서 없으면 객체 생성 후 저장한다.
        if(!friendshipReader.existPair(a, b)) {
            Friendship initfriendship = new Friendship(a, b);
            friendshipStore.store(initfriendship);
        }

        // 유저 리더에서 리시버 아이디를 받아온다.
        Profile profile = profileReader.getProfile(receiverId);

        return new FriendshipInfo.FriendDetail(profile);
    }

    @Override
    public FriendshipInfo.FriendListResponse getFriendList(Long userId) {
        // 리스트로 친구 목록과 정보 반환 --> n+1 방지해야함
        List<Long> friendsIds = friendshipReader.findFriendsByUserId(userId);

        // 바로 프로필에서 조회
        List<Profile> profile = profileReader.getProfilesByUserIds(friendsIds);

        List<FriendshipInfo.FriendDetail> friends = profile.stream()
                .map(FriendshipInfo.FriendDetail::new)
                .toList();

        return new FriendshipInfo.FriendListResponse(friends);
    }

    @Override
    public FriendshipInfo.FriendDetail getFriendDetail(Long userId) {
        // 프로필 리더로 프로필 정보 반환
        Profile profile = profileReader.getProfile(userId);

        return new FriendshipInfo.FriendDetail(profile);
    }

    @Override
    public FriendshipInfo.RandomFriendResponse getRandomFriend(Long userId) {
        // 친구 아이디 리스트 조회
        List<Long> friendIds = friendshipReader.findFriendsByUserId(userId);

        if (friendIds.isEmpty()) {
            return new FriendshipInfo.RandomFriendResponse(List.of());
        }

        // 친구 아이디를 셔플
        Collections.shuffle(friendIds);

        // 최대 5명만 추출
        List<Long> randomFiveIds = friendIds.stream()
                .limit(5)
                .toList();

        // 프로필을 한 번에 조회 (n+1 방지)
        List<Profile> profiles = profileReader.getProfilesByUserIds(randomFiveIds);

        List<FriendshipInfo.FriendDetail> items = profiles.stream()
                .map(FriendshipInfo.FriendDetail::new)
                .toList();

        return new FriendshipInfo.RandomFriendResponse(items);
    }
}
