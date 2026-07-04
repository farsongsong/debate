package com.example.portfolio.domain.user.service;

import com.example.portfolio.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailAsyncSender {
    private final MailService mailService;

    @Async
    public void sendVerificationCode(String email, String code) {
        try { mailService.sendVerificationCode(email, code); }
        catch (Exception e) { log.error("[MAIL] 인증 코드 발송 실패: {}", e.getMessage()); }
    }

    @Async
    public void sendWelcome(String email, String nickname) {
        try { mailService.sendWelcome(email, nickname); }
        catch (Exception e) { log.error("[MAIL] 환영 메일 발송 실패: {}", e.getMessage()); }
    }

    @Async
    public void sendPasswordReset(String email, String tempPassword) {
        try { mailService.sendPasswordReset(email, tempPassword); }
        catch (Exception e) { log.error("[MAIL] 비밀번호 재설정 메일 발송 실패: {}", e.getMessage()); }
    }
}
