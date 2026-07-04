package com.example.portfolio.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j @Service @RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}") private String baseUrl;
    @Value("${app.mail.from}") private String fromAddress;

    public void sendVerificationCode(String to, String code) {
        String html = """
            <div style="font-family:'Apple SD Gothic Neo',sans-serif;max-width:520px;margin:0 auto;
                        padding:32px 24px;background:#fff;border-radius:12px;border:1px solid #e2e8f0;">
              <h2 style="font-size:1.3rem;font-weight:700;color:#1a202c;margin-bottom:8px;">이메일 인증 코드</h2>
              <p style="color:#4a5568;font-size:.95rem;line-height:1.6;margin-bottom:24px;">
                아래 인증 코드를 입력해주세요. 코드는 <strong>10분</strong> 후 만료됩니다.
              </p>
              <div style="font-size:2.4rem;font-weight:800;letter-spacing:.5rem;color:#667eea;
                          background:#ebf4ff;border-radius:10px;padding:20px 0;text-align:center;margin-bottom:24px;">
                %s
              </div>
              <p style="font-size:.8rem;color:#a0aec0;">본인이 요청하지 않은 경우 이 메일을 무시해주세요.</p>
            </div>""".formatted(code);
        send(to, "이메일 인증 코드", html);
    }

    public void sendWelcome(String to, String nickname) {
        String html = """
            <div style="font-family:'Apple SD Gothic Neo',sans-serif;max-width:520px;margin:0 auto;
                        padding:32px 24px;background:#fff;border-radius:12px;border:1px solid #e2e8f0;">
              <h2 style="font-size:1.4rem;font-weight:700;color:#1a202c;margin-bottom:8px;">가입을 환영합니다 👋</h2>
              <p style="color:#4a5568;font-size:.95rem;line-height:1.6;">
                <strong>%s</strong>님, 토론 커뮤니티에 오신 것을 환영합니다!
              </p>
              <a href="%s" style="display:inline-block;margin-top:20px;padding:12px 28px;
                 background:#667eea;color:#fff;border-radius:8px;text-decoration:none;font-weight:600;">토론 시작하기</a>
            </div>""".formatted(nickname != null ? nickname : "회원", baseUrl);
        send(to, "가입을 환영합니다!", html);
    }

    public void sendPasswordReset(String to, String tempPassword) {
        String html = """
            <div style="font-family:'Apple SD Gothic Neo',sans-serif;max-width:520px;margin:0 auto;
                        padding:32px 24px;background:#fff;border-radius:12px;border:1px solid #e2e8f0;">
              <h2 style="font-size:1.4rem;font-weight:700;color:#1a202c;">임시 비밀번호 발급</h2>
              <p style="color:#4a5568;font-size:.95rem;line-height:1.6;margin:16px 0 8px;">
                아래 임시 비밀번호로 로그인 후 반드시 비밀번호를 변경해주세요.
              </p>
              <div style="font-size:1.5rem;font-weight:800;letter-spacing:.2rem;color:#e53e3e;
                          background:#fff5f5;border-radius:10px;padding:16px 0;text-align:center;margin:16px 0 24px;">
                %s
              </div>
              <a href="%s/auth/login" style="display:inline-block;padding:12px 28px;background:#667eea;
                 color:#fff;border-radius:8px;text-decoration:none;font-weight:600;">로그인하기</a>
            </div>""".formatted(tempPassword, baseUrl);
        send(to, "임시 비밀번호 발급", html);
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("[토론 커뮤니티] " + subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("[MAIL] 발송 완료 → {}", to);
        } catch (MessagingException e) {
            log.error("[MAIL] 발송 실패 → {} / {}", to, e.getMessage());
            throw new RuntimeException("메일 발송 실패", e);
        }
    }
}
