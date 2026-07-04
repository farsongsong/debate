package com.example.portfolio.domain.post.entity;

import com.example.portfolio.domain.tag.entity.Tag;
import com.example.portfolio.domain.user.entity.User;
import com.example.portfolio.domain.vote.entity.Vote;
import com.example.portfolio.domain.comment.entity.Comment;
import com.example.portfolio.domain.like.entity.PostLike;
import com.example.portfolio.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostStatus status = PostStatus.PENDING;

    @Builder.Default
    private int viewCount = 0;

    private String rejectReason;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // 태그
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();



    public void update(String title, String content, String summary, String thumbnail) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (summary != null) this.summary = summary;
        if (thumbnail != null) this.thumbnail = thumbnail;

        this.status = PostStatus.PENDING; // 수정 시 재승인
    }

    public void updateTags(Set<Tag> newTags) {
        this.tags.clear();
        this.tags.addAll(newTags);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void approve() {
        this.status = PostStatus.PUBLISHED;
        this.rejectReason = null;
    }

    public void reject(String reason) {
        this.status = PostStatus.REJECTED;
        this.rejectReason = reason;
    }

    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }
}