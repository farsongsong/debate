package com.example.portfolio.domain.post.controller;

import com.example.portfolio.domain.comment.service.CommentService;
import com.example.portfolio.domain.like.service.LikeService;
import com.example.portfolio.domain.post.dto.*;
import com.example.portfolio.domain.post.service.PostService;
import com.example.portfolio.domain.vote.service.VoteService;
import com.example.portfolio.global.security.PrincipalDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller @RequiredArgsConstructor @RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final VoteService voteService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) String tag,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        if (q != null && !q.isBlank()) {
            model.addAttribute("posts", postService.searchPosts(q, pageable));
            model.addAttribute("searchQuery", q);
        } else if (tag != null && !tag.isBlank()) {
            model.addAttribute("posts", postService.getPostsByTag(tag, pageable));
            model.addAttribute("currentTag", tag);
        } else {
            model.addAttribute("posts", postService.getPublishedPosts(pageable));
        }
        return "post/list";
    }

    // ── 내 게시글 목록 ───────────────────────────────────────────────
    @GetMapping("/my")
    public String myPosts(@RequestParam(defaultValue = "0") int page,
                          @AuthenticationPrincipal PrincipalDetail principal,
                          Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        model.addAttribute("posts", postService.getMyPosts(principal.getUser().getId(), pageable));
        return "post/my-posts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalDetail principal, Model model) {
        PostResponse post = postService.getPostAndIncrementView(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getComments(id));
        model.addAttribute("voteResult", voteService.getVoteResult(id));
        if (principal != null) {
            Long userId = principal.getUser().getId();
            model.addAttribute("liked", likeService.isLiked(id, userId));
            model.addAttribute("likeCount", likeService.getLikeCount(id));
            model.addAttribute("myVote", voteService.getMyVote(id, userId));
            model.addAttribute("isAdmin",
                    principal.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
        return "post/detail";
    }

    @GetMapping("/new")
    public String createForm() { return "post/form"; }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal PrincipalDetail principal,
                         @ModelAttribute PostRequest request,
                         @RequestParam(required = false) MultipartFile thumbnail) throws IOException {
        postService.createPost(principal.getUser().getId(), request, thumbnail);
        return "redirect:/posts/my?created=true";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal PrincipalDetail principal, Model model) {
        PostResponse post = postService.getPost(id);
        model.addAttribute("post", post);
        return "post/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalDetail principal,
                         @ModelAttribute PostRequest request,
                         @RequestParam(required = false) MultipartFile thumbnail) throws IOException {
        postService.updatePost(id, principal.getUser().getId(), request, thumbnail);
        return "redirect:/posts/my?updated=true";  // 내 글 목록으로
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalDetail principal) {
        postService.deletePost(id, principal.getUser().getId());
        return "redirect:/posts/my";
    }

    @GetMapping("/pending")
    public String pendingInfo() { return "post/pending"; }
}
