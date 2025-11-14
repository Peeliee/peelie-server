package com.peelie.friendship.domain;

import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileReader;
import com.peelie.user.domain.User;
import com.peelie.user.domain.UserReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipReader friendshipReader;
    private final FriendshipStore friendshipStore;
    private final ProfileReader profileReader;
    private final UserReader userReader;

    @Override
    @Transactional
    public FriendshipInfo.CreateFriendship createFriendship(Long senderId, Long receiverId) {
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

        return new FriendshipInfo.CreateFriendship(profile);
    }

    @Override
    public FriendshipInfo.FriendListResponse getFriendList(Long userId) {
        // 내 아이디 유저 컨텍스트 핸들러로
        Long myId = userId;

        //리스트로 친구 목록 반환
        <
        return null;
    }

    @Override
    public FriendshipInfo.GetFriendDetail getFriendDetail(Long userid) {
        // 프로필 리더로 프로필 정보 반환
        return null;
    }

    @Override
    public FriendshipInfo.RandomFriendResponse getRandomFriend(Long userid) {
        // 유저 아이디로 내 친구 목록 확인하고

        // 거기서 랜덤 n명 정보 리턴
        return null;
    }
}
