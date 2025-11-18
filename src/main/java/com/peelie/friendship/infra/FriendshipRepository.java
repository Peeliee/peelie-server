package com.peelie.friendship.infra;

import com.peelie.friendship.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
     List<Long> findFriendsByUserId(Long userId);
     boolean existsByUserId1AndUserId2(Long user1, Long user2);
     Friendship findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
