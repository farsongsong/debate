package com.example.portfolio.domain.comment.repository;

import com.example.portfolio.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.author LEFT JOIN FETCH c.replies r " +
           "LEFT JOIN FETCH r.author WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdWithReplies(@Param("postId") Long postId);
    long countByPostId(Long postId);
}
