package com.peelie.profile.domain;

import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile extends BaseTimeEntity {

    @Id
    private Long id;

    private String userName;

    private String profileImageUrl;

    private String instagramId;

    @Lob
    private String bio;

    @Enumerated(EnumType.STRING)
    private InteractionStyle interactionStyle;

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
        profile.id = userId;
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
        this.profileImageUrl = newImageUrl;
    }

    public void resetProfileImage() {
        this.profileImageUrl = null;
    }

    public void updateInstagramId(String newInstagramId) {
        this.instagramId = newInstagramId;
    }

}
