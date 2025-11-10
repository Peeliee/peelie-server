package com.peelie.friendship.infra;

import com.peelie.friendship.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

}
