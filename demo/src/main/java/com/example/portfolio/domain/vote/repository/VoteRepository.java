package com.example.portfolio.domain.vote.repository;

import com.example.portfolio.domain.vote.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPostIdAndUserId(Long postId, Long userId);
    long countByPostIdAndVoteType(Long postId, VoteType voteType);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);
}
