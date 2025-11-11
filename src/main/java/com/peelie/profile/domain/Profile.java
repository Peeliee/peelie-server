package com.peelie.profile.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile extends BaseTimeEntity {
    // TODO: 추후 S3에 저장된 기본 이미지 주소로 변경
    private static final String DEFAULT_IMAGE_URL = "DEFAULT_IMAGE_URL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String profileImageUrl; // nullable

    private String instagramId;

    @Lob
    private String stage1Bio;

    @Lob
    private String stage2Bio;

    @Lob
    private String stage3Bio;

    @Column(unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private InteractionStyle interactionStyle;

    @ElementCollection
    @CollectionTable(name = "profile_interest_categories", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "category_id")
    private Set<Long> interestCategoryIds = new HashSet<>();

    @Builder
    public Profile(Long userId, String userName, String profileImageUrl, String instagramId) {
        if (userName == null || userName.isBlank()) {
            throw new BaseException("회원 이름이 입력되지 않았습니다", ErrorCode.VALIDATION_ERROR);
        }

        this.userId = userId;
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.instagramId = instagramId;
        this.interactionStyle = InteractionStyle.UNKNOWN;
    }

    // 도메인 메서드
    public void changeName(String newUserName) {
        if (newUserName == null || newUserName.isBlank()) {
            throw new BaseException("회원 이름이 입력되지 않았습니다", ErrorCode.VALIDATION_ERROR);
        }
        this.userName = newUserName;
    }

    public void changeProfileImage(String newImageUrl) {
        if (newImageUrl == null || newImageUrl.isEmpty()) {
            this.profileImageUrl = null;
            return;
        }
        this.profileImageUrl = newImageUrl;
    }

    public void resetProfileImage() {
        this.profileImageUrl = DEFAULT_IMAGE_URL;
    }

    public void changeInstagramId(String newInstagramId) {
        this.instagramId = newInstagramId;
    }

    public void changeInteractionStyle(InteractionStyle newInteractionStyle) {
        this.interactionStyle = Objects.requireNonNull(newInteractionStyle);
    }

    public void changeStage1Bio(String newStage1Bio) {
        this.stage1Bio = newStage1Bio;
    }

    public void changeStage2Bio(String newStage2Bio) {
        this.stage2Bio = newStage2Bio;
    }

    public void changeStage3Bio(String newStage3Bio) {
        this.stage3Bio = newStage3Bio;
    }

    // public void applyOnboarding(Set<Long> categoryIds, InteractionStyle style,
    // String bio) {
    // this.interestCategoryIds.clear();
    // if (categoryIds != null) {
    // this.interestCategoryIds.addAll(categoryIds);
    // }
    // this.interactionStyle = (style == null) ? InteractionStyle.UNKNOWN : style;
    // updateBio(bio);
    // }
}