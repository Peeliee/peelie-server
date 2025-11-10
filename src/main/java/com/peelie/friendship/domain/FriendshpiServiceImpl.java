package com.peelie.friendship.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendshpiServiceImpl implements FriendshipService {

    @Override
    public FriendshipInfo.CreateFriendship createFriendship(FriendshipCommand.CreateFriendship command) {
        return null;
    }
}
