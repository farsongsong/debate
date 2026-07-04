package com.example.portfolio.domain.user.dto;

import com.example.portfolio.domain.user.entity.User;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private final Long id;
    private final String username, email, nickname, profileImage, bio, role;
    private final LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname() != null ? user.getNickname() : user.getUsername();
        this.profileImage = user.getProfileImage();
        this.bio = user.getBio();
        this.role = user.getRole().name();
        this.createdAt = user.getCreatedAt();
    }
}
