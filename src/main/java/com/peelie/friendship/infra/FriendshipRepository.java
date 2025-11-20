package com.peelie.friendship.infra;

import com.peelie.friendship.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
     List<Friendship> findFriendsBySenderIdOrReceiverId(Long senderId, Long receiverId);
     boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
     Optional<Friendship> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
