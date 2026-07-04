package com.example.portfolio.domain.user.repository;

import com.example.portfolio.domain.user.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    // 가장 최근 발급된 코드 조회
    Optional<EmailVerificationToken> findTopByEmailOrderByIdDesc(String email);
    boolean existsByEmailAndVerifiedTrue(String email);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.email = :email")
    void deleteAllByEmail(String email);
}
