package com.example.portfolio.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "email_verification_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 구버전 DB 호환용 (nullable)
    @Column
    private String token;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean verified = false;

    public static EmailVerificationToken create(String email) {
        EmailVerificationToken t = new EmailVerificationToken();
        t.code = String.format("%06d", new Random().nextInt(1000000));
        t.token = t.code; // 구버전 컬럼도 채워줌
        t.email = email;
        t.expiresAt = LocalDateTime.now().plusMinutes(10);
        return t;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean matches(String inputCode) {
        return this.code.equals(inputCode.trim());
    }

    public void verify() {
        this.verified = true;
    }
}
