package com.peelie.profile.interfaces;

public class ProfileDto {
    public record RegisterProfileRequest(
            Long userId,
            String userName,
            String instagramId,
            String imageUrl
    ) {}

    public record UpdateProfileNameRequest(
            Long userId,
            String newName
    ) {}

    public record UpdateInstagramRequest(
            Long userId,
            String newInstagramId
    ) {}

    public record UpdateImageUrlRequest(
            Long userId,
            String newImgUrl
    ) {}

    public record resetProfileImageRequest(
            Long userId,
            String newImgUrl
    ) {}
}
