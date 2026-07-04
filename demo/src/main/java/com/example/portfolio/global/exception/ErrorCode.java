package com.example.portfolio.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상이어야 합니다."),
    PASSWORD_TOO_WEAK(HttpStatus.BAD_REQUEST, "비밀번호는 영문, 숫자를 포함해야 합니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),
    EMAIL_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "인증 코드를 먼저 발송해주세요."),
    EMAIL_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 인증 코드입니다. 다시 발송해주세요."),
    EMAIL_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "게시글에 접근 권한이 없습니다."),
    POST_PENDING(HttpStatus.FORBIDDEN, "관리자 승인 대기 중인 게시글입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "댓글에 접근 권한이 없습니다."),
    VOTE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 투표하셨습니다."),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "파일 크기는 5MB 이하여야 합니다."),
    FILE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드 가능합니다. (jpg, png, gif, webp)"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
