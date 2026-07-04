package com.example.portfolio.domain.post.dto;

import com.example.portfolio.domain.post.entity.Post;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class PostResponse {
    private final Long id;
    private final String title, content, summary, thumbnail, status, authorName, rejectReason;
    private final Long authorId;
    private final int viewCount;
    private final Set<String> tags;
    private final LocalDateTime createdAt, updatedAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.summary = post.getSummary();
        this.thumbnail = post.getThumbnail();
        this.status = post.getStatus().name();
        this.rejectReason = post.getRejectReason();
        this.authorName = post.getAuthor().getNickname() != null
                ? post.getAuthor().getNickname() : post.getAuthor().getUsername();
        this.authorId = post.getAuthor().getId();
        this.viewCount = post.getViewCount();
        this.tags = post.getTags().stream().map(t -> t.getName()).collect(Collectors.toSet());
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
