package com.example.portfolio.web.controller;

import com.example.portfolio.domain.like.service.LikeService;
import com.example.portfolio.global.common.ResponseDto;
import com.example.portfolio.global.security.PrincipalDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequiredArgsConstructor @RequestMapping("/api")
public class LikeApiController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ResponseDto<Map<String, Object>>> toggleLike(
            @PathVariable Long postId, @AuthenticationPrincipal PrincipalDetail principal) {
        boolean liked = likeService.toggle(postId, principal.getUser().getId());
        long count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(ResponseDto.success(Map.of("liked", liked, "count", count)));
    }
}
