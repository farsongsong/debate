package com.example.portfolio.domain.admin.service;

import com.example.portfolio.domain.post.entity.*;
import com.example.portfolio.domain.post.repository.PostRepository;
import com.example.portfolio.domain.user.entity.User;
import com.example.portfolio.domain.user.repository.UserRepository;
import com.example.portfolio.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class AdminService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Page<Post> getPendingPosts(Pageable pageable) {
        return postRepository.findByStatusOrderByCreatedAtDesc(PostStatus.PENDING, pageable);
    }

    public Page<Post> getAllPosts(PostStatus status, Pageable pageable) {
        if (status != null) return postRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return postRepository.findAll(pageable);
    }

    public Page<User> getUsers(Pageable pageable) { return userRepository.findAll(pageable); }

    public long countPending() { return postRepository.countByStatus(PostStatus.PENDING); }

    @Transactional
    public void approvePost(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND))
                .approve();
    }

    @Transactional
    public void rejectPost(Long postId, String reason) {
        postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND))
                .reject(reason);
    }

    @Transactional
    public void deletePost(Long postId) { postRepository.deleteById(postId); }

    @Transactional
    public void promoteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).promoteToAdmin();
    }

    @Transactional
    public void disableUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).disable();
    }
}
