package com.example.portfolio.domain.post.service;

import com.example.portfolio.domain.post.dto.*;
import com.example.portfolio.domain.post.entity.*;
import com.example.portfolio.domain.post.repository.PostRepository;
import com.example.portfolio.domain.tag.entity.Tag;
import com.example.portfolio.domain.tag.service.TagService;
import com.example.portfolio.domain.user.entity.Role;
import com.example.portfolio.domain.user.entity.User;
import com.example.portfolio.domain.user.repository.UserRepository;
import com.example.portfolio.global.exception.*;
import com.example.portfolio.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public Page<PostResponse> getPublishedPosts(Pageable pageable) {
        return postRepository.findByStatusOrderByCreatedAtDesc(PostStatus.PUBLISHED, pageable).map(PostResponse::new);
    }

    public Page<PostResponse> searchPosts(String query, Pageable pageable) {
        return postRepository.search(query, pageable).map(PostResponse::new);
    }

    public Page<PostResponse> getPostsByTag(String tagName, Pageable pageable) {
        return postRepository.findByTagName(tagName, pageable).map(PostResponse::new);
    }

    public Page<PostResponse> getMyPosts(Long userId, Pageable pageable) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable).map(PostResponse::new);
    }

    // 수정 폼용 - 본인 글은 상태 무관하게 조회 가능
    public PostResponse getPost(Long id) {
        return new PostResponse(postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)));
    }

    @Transactional
    public PostResponse getPostAndIncrementView(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        if (post.getStatus() != PostStatus.PUBLISHED) throw new CustomException(ErrorCode.POST_NOT_FOUND);
        post.incrementViewCount();
        return new PostResponse(post);
    }

    @Transactional
    public Long createPost(Long authorId, PostRequest request, MultipartFile thumbnail) throws IOException {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String thumbPath = (thumbnail != null && !thumbnail.isEmpty()) ? FileUtil.saveFile(thumbnail) : null;
        String summary = (request.getSummary() != null && !request.getSummary().isBlank())
                ? request.getSummary()
                : request.getContent().substring(0, Math.min(200, request.getContent().length()));
        Post post = Post.builder()
                .title(request.getTitle()).content(request.getContent())
                .summary(summary).thumbnail(thumbPath).author(author)
                .tags(buildTags(request.getTags())).status(PostStatus.PENDING).build();
        return postRepository.save(post).getId();
    }

    @Transactional
    public void updatePost(Long postId, Long userId, PostRequest request, MultipartFile thumbnail) throws IOException {
        Post post = findOwnedPost(postId, userId);
        String thumbPath = (thumbnail != null && !thumbnail.isEmpty()) ? FileUtil.saveFile(thumbnail) : null;
        post.update(request.getTitle(), request.getContent(), request.getSummary(), thumbPath);
        post.updateTags(buildTags(request.getTags()));
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        if (user.getRole() != Role.ADMIN && !post.isAuthor(userId))
            throw new CustomException(ErrorCode.POST_ACCESS_DENIED);
        postRepository.delete(post);
    }

    private Post findOwnedPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        if (!post.isAuthor(userId)) throw new CustomException(ErrorCode.POST_ACCESS_DENIED);
        return post;
    }

    private Set<Tag> buildTags(List<String> tagNames) {
        if (tagNames == null) return new HashSet<>();
        return tagNames.stream().filter(t -> !t.isBlank()).map(tagService::findOrCreate).collect(Collectors.toSet());
    }
}
