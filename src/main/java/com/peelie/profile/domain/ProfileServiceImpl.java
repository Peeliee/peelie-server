package com.peelie.profile.domain;

import com.peelie.user.domain.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileReader profileReader;
    private final ProfileStore profileStore;
    private final UserReader userReader;

    @Override
    @Transactional
    public ProfileInfo registerProfile(ProfileCommand command) {
        // 1. 사용자가 입력값들을 입력한다. - 파라미터
        // 2. 입력값들로 Profile 엔티티 객체를 생성한다.
        Profile initProfile = command.toEntity();
        // 3. 생성된 profile 객체를 DB에 저장한다.
        Profile profile = profileStore.store(initProfile);
        // 4. 생성된 Profile 객체정보를 바탕으로 ProfileInfo 객체를 반환한다.
        return new ProfileInfo(profile);
    }

    @Override
    public ProfileInfo getProfile(Long profileId) {
        // 1. DB에서 해당 Id값을 가진 프로필을 찾는다
        Profile profile = profileReader.getProfile(profileId);
        // 2. profileInfo로 변환해서 리턴
        return new ProfileInfo(profile);
    }

    @Override
    public Long getProfileIdByUserId(Long userId) {
        // 1. Db에서 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 위에서 찾은 프로필 id 찾기
        return profile.getId();
    }

    @Override
    @Transactional
    public void updateProfileName(Long userId, String newUserName) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfile(userId);
        // 2. 찾은 프로필의 이름을 새로운 newUserName으로 바꾼다.
        profile.updateName(newUserName);
    }

    @Override
    @Transactional
    public void updateInstagramId(Long userId, String newInstagramId) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 찾은 프로필의 인스타 id를 바꾼다.
        profile.updateInstagramId(newInstagramId);
    }

    @Override
    @Transactional
    public void resetProfileImage(Long userId) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 찾은 프로필의 인스타 id를 바꾼다.
        profile.resetProfileImage();
    }

    @Override
    @Transactional
    public void updateProfileImage(Long userId, String newProfileImageUrl) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 찾은 프로필의 프사를 바꾼다.
        profile.changeProfileImage(newProfileImageUrl);
    }

    @Override
    @Transactional
    public void updateInteractionStyle(Long userId, InteractionStyle interactionStyle) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 찾은 프로필의 교류 성향을 업데이트한다.
        profile.updateInteractionStyle(interactionStyle);
    }

    @Override
    @Transactional
    public void updateBio(Long userId, String bio) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 찾은 프로필의 한줄소개를 업데이트한다.
        profile.updateBio(bio);
    }
}

