package com.example.portfolio.domain.user.entity;

import com.example.portfolio.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "users") @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor @Builder
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false, length = 50) private String username;
    @Column(nullable = false) private String password;
    @Column(unique = true, nullable = false) private String email;
    @Column(length = 100) private String nickname;
    private String profileImage;
    @Column(columnDefinition = "TEXT") private String bio;
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default private Role role = Role.USER;
    @Builder.Default private boolean enabled = true;

    public void updateProfile(String nickname, String bio, String profileImage) {
        if (nickname != null) this.nickname = nickname;
        if (bio != null) this.bio = bio;
        if (profileImage != null) this.profileImage = profileImage;
    }
    public void changePassword(String encodedPassword) { this.password = encodedPassword; }
    public void promoteToAdmin() { this.role = Role.ADMIN; }
    public void disable() { this.enabled = false; }
}
