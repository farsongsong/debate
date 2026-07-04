package com.example.portfolio.domain.post.entity;

public enum PostStatus {
    PENDING,    // 관리자 승인 대기
    PUBLISHED,  // 승인됨 (공개)
    REJECTED,   // 거부됨
    DELETED     // 삭제됨
}
