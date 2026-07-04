package com.example.portfolio.domain.comment.service;

import com.example.portfolio.domain.comment.dto.*;
import com.example.portfolio.domain.comment.entity.*;
import com.example.portfolio.domain.comment.repository.CommentRepository;
import com.example.portfolio.domain.post.repository.PostRepository;
import com.example.portfolio.domain.user.repository.UserRepository;
import com.example.portfolio.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdWithReplies(postId)
                .stream().map(CommentResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse addComment(Long postId, Long userId, CommentRequest request) {
        var post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        var user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        }
        CommentSide side = CommentSide.NEUTRAL;
        if (request.getSide() != null) {
            try { side = CommentSide.valueOf(request.getSide()); } catch (Exception ignored) {}
        }
        return new CommentResponse(commentRepository.save(
                Comment.builder().content(request.getContent()).post(post).author(user).parent(parent).side(side).build()));
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.isAuthor(userId)) throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        comment.delete();
    }
}
