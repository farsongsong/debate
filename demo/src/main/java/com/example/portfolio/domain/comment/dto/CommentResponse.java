package com.example.portfolio.domain.comment.dto;

import com.example.portfolio.domain.comment.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentResponse {
    private final Long id;
    private final String content, authorName, authorImage, side;
    private final Long authorId;
    private final boolean deleted;
    private final LocalDateTime createdAt;
    private final List<CommentResponse> replies;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorName = comment.getAuthor().getNickname() != null
                ? comment.getAuthor().getNickname() : comment.getAuthor().getUsername();
        this.authorId = comment.getAuthor().getId();
        this.authorImage = comment.getAuthor().getProfileImage();
        this.side = comment.getSide().name();
        this.deleted = comment.isDeleted();
        this.createdAt = comment.getCreatedAt();
        this.replies = comment.getReplies().stream().map(CommentResponse::new).collect(Collectors.toList());
    }
}
