package com.example.portfolio.domain.like.entity;

import com.example.portfolio.domain.post.entity.Post;
import com.example.portfolio.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "post_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id","user_id"}))
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor @Builder
public class PostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id") private Post post;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
}
