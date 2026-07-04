package com.example.portfolio.domain.comment.entity;

import com.example.portfolio.domain.post.entity.Post;
import com.example.portfolio.domain.user.entity.User;
import com.example.portfolio.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity @Table(name = "comments") @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor @Builder
public class Comment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommentSide side = CommentSide.NEUTRAL;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false) private Post post;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User author;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id") private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<Comment> replies = new ArrayList<>();

    @Builder.Default private boolean deleted = false;

    public void update(String content) { this.content = content; }
    public void delete() { this.deleted = true; this.content = "삭제된 댓글입니다."; }
    public boolean isAuthor(Long userId) { return this.author.getId().equals(userId); }
}
