package com.example.portfolio.web.controller;

import com.example.portfolio.domain.post.dto.PostResponse;
import com.example.portfolio.domain.post.entity.PostStatus;
import com.example.portfolio.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostRepository postRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recentPosts",
                postRepository.findByStatusOrderByCreatedAtDesc(
                                PostStatus.PUBLISHED,
                                PageRequest.of(0, 6, Sort.by("createdAt").descending()))
                        .stream()
                        .map(PostResponse::new)
                        .collect(Collectors.toList()));
        return "index";
    }
}