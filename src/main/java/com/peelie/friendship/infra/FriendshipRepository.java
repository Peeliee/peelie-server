package com.peelie.friendship.infra;

import com.peelie.friendship.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
     Optional<Friendship> getFriendshipById(Long userId);
}
