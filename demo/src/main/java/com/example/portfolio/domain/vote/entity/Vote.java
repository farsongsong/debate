package com.example.portfolio.domain.vote.entity;

import com.example.portfolio.domain.post.entity.Post;
import com.example.portfolio.domain.user.entity.User;
import com.example.portfolio.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "votes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id","user_id"}))
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor @Builder
public class Vote extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false) private Post post;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;

    @Enumerated(EnumType.STRING) @Column(nullable = false) private VoteType voteType;

    public void changeVote(VoteType voteType) { this.voteType = voteType; }
}
