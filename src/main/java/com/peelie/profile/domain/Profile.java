package com.peelie.profile.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile extends BaseTimeEntity {

    @Id
    private Long id;

    private String userName;

    private String profileImageUrl; //nullable

    private String instagramId;

    @Lob
    private String bio;

    @Enumerated(EnumType.STRING)
    private InteractionStyle interactionStyle = InteractionStyle.UNKNOWN;

    @ElementCollection
    @CollectionTable(
            name = "profile_interest_categories",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "category_id")
    private Set<Long> interestCategoryIds;

    //생성 메서드
    public static Profile create(Long userId) {
        Profile profile = new Profile();
        profile.id = Objects.requireNonNull(userId, "userId must not be null");
        profile.interactionStyle = InteractionStyle.UNKNOWN;
        return profile;
    }

    //도메인 메서드
    public void updateName(String newUserName) {
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