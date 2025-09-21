package com.peelie.user.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.common.util.TokenGenerator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {
    private static final String DEFAULT_IMAGE_URL = "default_profile_img_url";
    private static final String PREFIX_USER = "usr_";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userToken;

    private User(String userToken) {
        this.userToken = userToken;
    }

    public static User create() {
        var userToken = TokenGenerator.randomCharacterWithPrefix(PREFIX_USER);
        return new User(userToken);
    }
}