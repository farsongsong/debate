package com.example.portfolio.domain.vote.controller;

import com.example.portfolio.domain.vote.service.VoteService;
import com.example.portfolio.global.common.ResponseDto;
import com.example.portfolio.global.security.PrincipalDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequiredArgsConstructor @RequestMapping("/api/posts/{postId}/vote")
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<ResponseDto<VoteService.VoteResult>> vote(
            @PathVariable Long postId,
            @AuthenticationPrincipal PrincipalDetail principal,
            @RequestBody Map<String, String> body) {
        VoteService.VoteResult result = voteService.vote(postId, principal.getUser().getId(), body.get("voteType"));
        return ResponseEntity.ok(ResponseDto.success(result));
    }
}
