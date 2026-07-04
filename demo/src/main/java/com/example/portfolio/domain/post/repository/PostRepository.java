package com.example.portfolio.domain.post.repository;

import com.example.portfolio.domain.post.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Post> search(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName AND p.status = 'PUBLISHED'")
    Page<Post> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    long countByStatus(PostStatus status);
}
