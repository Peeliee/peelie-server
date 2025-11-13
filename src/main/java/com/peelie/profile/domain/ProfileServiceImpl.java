package com.peelie.profile.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper; // 추가

    @Override
    @Transactional
    public ProfileInfo registerProfile(ProfileCommand.RegisterCommand command) {
        // 1. 사용자가 입력값들을 입력한다. - 파라미터
        // 2. 입력값들로 Profile 엔티티 객체를 생성한다.
        Profile initProfile = command.toEntity();
        // 3. 생성된 profile 객체를 DB에 저장한다.
        Profile profile = profileStore.store(initProfile);
        // 4. 생성된 Profile 객체정보를 바탕으로 ProfileInfo 객체를 반환한다.
        return new ProfileInfo(profile,objectMapper);
    }

    @Override
    public ProfileInfo getProfile(Long profileId) {
        // 1. DB에서 해당 Id값을 가진 프로필을 찾는다
        Profile profile = profileReader.getProfile(profileId);
        // 2. profileInfo로 변환해서 리턴
        return new ProfileInfo(profile,objectMapper);
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
    public void resetProfileImage(Long userId) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 찾은 프로필의 인스타 id를 바꾼다.
        profile.resetProfileImage();
    }

    @Override
    @Transactional
    public void updateInteractionStyle(Long userId, String newInteractionStyle) {
        Profile  profile = profileReader.getProfileByUserId(userId);

        profile.changeInteractionStyle(InteractionStyle.valueOf(newInteractionStyle));
    }

    @Override
    @Transactional
    public ProfileInfo updateMyProfile(Long userId, ProfileCommand.UpdateCommand command) {
        // 1. 해당 userId를 가진 프로필을 조회한다.
        Profile profile = profileReader.getProfileByUserId(userId);
        // 2. 찾은 프로필의 이름을 새로운 newUserName으로 바꾼다.
        profile.changeName(command.getUserName());
        profile.changeInstagramId(command.getInstagramId());
        profile.changeProfileImage(command.getImageUrl());
        profile.changeInteractionStyle(command.getInteractionStyle());
        profile.changeStage1Bio(command.getStage1Bio());
        profile.changeStage2Bio(command.getStage2Bio());
        profile.changeStage3Bio(command.getStage3Bio());

        return new ProfileInfo(profile,objectMapper);
    }
}
