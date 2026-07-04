package com.example.portfolio.domain.like.repository;

import com.example.portfolio.domain.like.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
}
