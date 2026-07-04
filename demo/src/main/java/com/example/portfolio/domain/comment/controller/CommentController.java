package com.example.portfolio.domain.comment.controller;

import com.example.portfolio.domain.comment.dto.*;
import com.example.portfolio.domain.comment.service.CommentService;
import com.example.portfolio.global.common.ResponseDto;
import com.example.portfolio.global.security.PrincipalDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequiredArgsConstructor @RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<CommentResponse>>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(ResponseDto.success(commentService.getComments(postId)));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<CommentResponse>> addComment(
            @PathVariable Long postId, @AuthenticationPrincipal PrincipalDetail principal,
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(ResponseDto.success("댓글이 등록되었습니다.",
                commentService.addComment(postId, principal.getUser().getId(), request)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto<Void>> deleteComment(
            @PathVariable Long postId, @PathVariable Long commentId,
            @AuthenticationPrincipal PrincipalDetail principal) {
        commentService.deleteComment(commentId, principal.getUser().getId());
        return ResponseEntity.ok(ResponseDto.success(null));
    }
}
