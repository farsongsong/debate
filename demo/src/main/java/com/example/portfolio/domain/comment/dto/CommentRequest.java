package com.example.portfolio.domain.comment.dto;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class CommentRequest {
    private String content;
    private Long parentId;
    private String side; // AGREE, DISAGREE, NEUTRAL
}
