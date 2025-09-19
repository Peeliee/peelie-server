package com.peelie.user.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.common.util.TokenGenerator;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(nullable = false)
    private String userName;

    private String imageUrl;
    private String instagramId;

    @Lob
    private String bio;

    @Builder
    public User(String userName, String imageUrl, String instagramId, String bio) {
        if (StringUtils.isEmpty(userName)) throw new BaseException("이름을 입력해주세요", ErrorCode.VALIDATION_ERROR);

        this.userToken = TokenGenerator.randomCharacterWithPrefix(PREFIX_USER);
        this.userName = userName;
        this.instagramId = instagramId;
        this.bio = bio;
        if (!StringUtils.isBlank(imageUrl)) {
            this.imageUrl = imageUrl;
        } else {
            this.imageUrl = DEFAULT_IMAGE_URL;
        }
    }

}