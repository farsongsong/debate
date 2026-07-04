package com.example.portfolio.domain.like.service;

import com.example.portfolio.domain.like.entity.PostLike;
import com.example.portfolio.domain.like.repository.LikeRepository;
import com.example.portfolio.domain.post.repository.PostRepository;
import com.example.portfolio.domain.user.repository.UserRepository;
import com.example.portfolio.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public boolean isLiked(Long postId, Long userId) { return likeRepository.existsByPostIdAndUserId(postId, userId); }
    public long getLikeCount(Long postId) { return likeRepository.countByPostId(postId); }

    @Transactional
    public boolean toggle(Long postId, Long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .map(like -> { likeRepository.delete(like); return false; })
                .orElseGet(() -> {
                    var post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
                    var user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    likeRepository.save(PostLike.builder().post(post).user(user).build());
                    return true;
                });
    }
}
