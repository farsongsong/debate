package com.example.portfolio.domain.user.controller;

import com.example.portfolio.domain.user.dto.*;
import com.example.portfolio.domain.user.service.UserService;
import com.example.portfolio.global.exception.CustomException;
import com.example.portfolio.global.security.PrincipalDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

@Controller @RequiredArgsConstructor @RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUser(id));
        return "user/profile";
    }

    @GetMapping("/settings")
    public String settingsForm(@AuthenticationPrincipal PrincipalDetail principal, Model model) {
        model.addAttribute("user", userService.getUser(principal.getUser().getId()));
        return "user/settings";
    }

    @PostMapping("/settings")
    public String updateProfile(@AuthenticationPrincipal PrincipalDetail principal,
                                @ModelAttribute UserUpdateRequest request,
                                @RequestParam(required = false) MultipartFile profileImage,
                                RedirectAttributes ra) throws IOException {
        try {
            userService.updateProfile(principal.getUser().getId(), request, profileImage);
            ra.addAttribute("success", true);
        } catch (CustomException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users/settings";
    }

    @PostMapping("/password")
    public String changePassword(@AuthenticationPrincipal PrincipalDetail principal,
                                 @ModelAttribute PasswordChangeRequest request,
                                 RedirectAttributes ra) {
        try {
            userService.changePassword(principal.getUser().getId(), request);
            ra.addAttribute("pwSuccess", true);
        } catch (CustomException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users/settings";
    }
}
