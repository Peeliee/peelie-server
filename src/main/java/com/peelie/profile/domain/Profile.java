package com.peelie.profile.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jpa.BaseTimeEntity;
import com.peelie.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String profileImageUrl; //nullable

    private String instagramId;

    @Lob
    private String bio;

    @Column(unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private InteractionStyle interactionStyle;

    @ElementCollection
    @CollectionTable(
            name = "profile_interest_categories",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "category_id")
    private Set<Long> interestCategoryIds = new HashSet<>();

    @Builder
    public Profile(Long userId, String userName, String profileImageUrl, String instagramId, String bio) {
        if (userName.isEmpty()) throw new BaseException("회원 이름이 입력되지 않았습니다", ErrorCode.VALIDATION_ERROR);

        this.userId = userId;
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.instagramId = instagramId;
        this.bio = bio;
        this.interactionStyle = InteractionStyle.UNKNOWN;
    }

    //도메인 메서드
    public void updateName(String newUserName) {
        if (newUserName.isBlank() || newUserName.isEmpty() || newUserName==null) {
            throw new BaseException("회원 이름이 입력되지 않았습니다", ErrorCode.VALIDATION_ERROR);
        }
        this.userName = newUserName;
    }

    public void updateBio(String newBio) {
        this.bio = newBio;
    }

    public void changeProfileImage(String newImageUrl) {
        if (newImageUrl == null || newImageUrl.isEmpty()) {
            this.profileImageUrl = null;
            return;
        }
        this.profileImageUrl = newImageUrl;
    }

    public void resetProfileImage() {
        this.profileImageUrl = null;
    }

    public void updateInstagramId(String newInstagramId) {
        this.instagramId = newInstagramId;
    }

    public void applyOnboarding(Set<Long> categoryIds, InteractionStyle style, String bio) {
        this.interestCategoryIds.clear();
        if (categoryIds != null) {
            this.interestCategoryIds.addAll(categoryIds);
        }
        this.interactionStyle = (style == null) ? InteractionStyle.UNKNOWN : style;
        updateBio(bio);
    }
}