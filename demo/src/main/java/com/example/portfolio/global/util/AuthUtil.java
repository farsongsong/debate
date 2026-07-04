package com.example.portfolio.global.util;

import com.example.portfolio.global.security.PrincipalDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public class AuthUtil {
    public static Optional<PrincipalDetail> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        if (auth.getPrincipal() instanceof PrincipalDetail detail) return Optional.of(detail);
        return Optional.empty();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().map(d -> d.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("로그인이 필요합니다."));
    }
}
