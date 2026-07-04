package com.example.portfolio.web.controller;

import com.example.portfolio.domain.user.dto.SignupRequest;
import com.example.portfolio.domain.user.service.UserService;
import com.example.portfolio.global.common.ResponseDto;
import com.example.portfolio.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller @RequiredArgsConstructor @RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String registered,
                            Model model) {
        if (error != null) model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        if (registered != null) model.addAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("request", new SignupRequest());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequest request, Model model) {
        if (!request.isPasswordMatch()) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "auth/signup";
        }
        try {
            userService.signup(request);
            return "redirect:/auth/login?registered=true";
        } catch (CustomException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/signup";
        } catch (Exception e) {
            model.addAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "auth/signup";
        }
    }

    // ── 비밀번호 찾기 페이지 ─────────────────────────────────────────
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() { return "auth/forgot-password"; }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        try {
            userService.sendPasswordReset(email);
            model.addAttribute("success", "임시 비밀번호를 이메일로 발송했습니다.");
        } catch (CustomException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "처리 중 오류가 발생했습니다.");
        }
        return "auth/forgot-password";
    }

    // ── 인증 코드 발송 ───────────────────────────────────────────────
    @PostMapping("/send-verification")
    @ResponseBody
    public ResponseEntity<ResponseDto<Void>> sendVerification(@RequestParam String email) {
        try {
            userService.sendVerificationCode(email);
            return ResponseEntity.ok(ResponseDto.success("인증 코드를 발송했습니다.", null));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getStatus()).body(ResponseDto.fail(e.getMessage()));
        }
    }

    // ── 인증 코드 확인 ───────────────────────────────────────────────
    @PostMapping("/verify-code")
    @ResponseBody
    public ResponseEntity<ResponseDto<Void>> verifyCode(@RequestParam String email,
                                                         @RequestParam String code) {
        try {
            userService.verifyCode(email, code);
            return ResponseEntity.ok(ResponseDto.success("이메일 인증이 완료되었습니다.", null));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getStatus()).body(ResponseDto.fail(e.getMessage()));
        }
    }

    // ── 중복 확인 API ────────────────────────────────────────────────
    @GetMapping("/check/username")
    @ResponseBody
    public ResponseEntity<ResponseDto<Void>> checkUsername(@RequestParam String username) {
        if (userService.isUsernameTaken(username))
            return ResponseEntity.status(409).body(ResponseDto.fail("이미 사용 중인 아이디입니다."));
        return ResponseEntity.ok(ResponseDto.success("사용 가능한 아이디입니다.", null));
    }

    @GetMapping("/check/email")
    @ResponseBody
    public ResponseEntity<ResponseDto<Void>> checkEmail(@RequestParam String email) {
        if (userService.isEmailTaken(email))
            return ResponseEntity.status(409).body(ResponseDto.fail("이미 가입된 이메일입니다."));
        return ResponseEntity.ok(ResponseDto.success("사용 가능한 이메일입니다.", null));
    }

    @GetMapping("/check/nickname")
    @ResponseBody
    public ResponseEntity<ResponseDto<Void>> checkNickname(@RequestParam String nickname) {
        if (userService.isNicknameTaken(nickname))
            return ResponseEntity.status(409).body(ResponseDto.fail("이미 사용 중인 닉네임입니다."));
        return ResponseEntity.ok(ResponseDto.success("사용 가능한 닉네임입니다.", null));
    }
}
