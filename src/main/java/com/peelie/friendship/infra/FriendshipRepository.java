package com.peelie.friendship.infra;

import com.peelie.friendship.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
     List<Friendship> findFriendsBySenderIdOrReceiverId(Long senderId, Long receiverId);
     boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
     Friendship findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
