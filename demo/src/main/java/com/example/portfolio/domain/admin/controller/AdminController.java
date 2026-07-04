package com.example.portfolio.domain.admin.controller;

import com.example.portfolio.domain.admin.service.AdminService;
import com.example.portfolio.domain.post.entity.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pendingPosts", adminService.getPendingPosts(PageRequest.of(0, 10)));
        model.addAttribute("pendingCount", adminService.countPending());
        model.addAttribute("users", adminService.getUsers(PageRequest.of(0, 5)));
        return "admin/dashboard";
    }

    @GetMapping("/posts")
    public String posts(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String status,
                        Model model) {
        PostStatus postStatus = null;
        try { if (status != null) postStatus = PostStatus.valueOf(status); } catch (Exception ignored) {}
        model.addAttribute("posts", adminService.getAllPosts(postStatus, PageRequest.of(page, 20)));
        model.addAttribute("currentStatus", status);
        model.addAttribute("pendingCount", adminService.countPending());
        return "admin/posts";
    }

    @GetMapping("/pending")
    public String pending(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("posts", adminService.getPendingPosts(PageRequest.of(page, 20)));
        model.addAttribute("pendingCount", adminService.countPending());
        return "admin/pending";
    }

    @PostMapping("/posts/{id}/approve")
    public String approve(@PathVariable Long id) {
        adminService.approvePost(id);
        return "redirect:/admin/pending?approved=true";
    }

    @PostMapping("/posts/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(defaultValue = "부적절한 내용") String reason) {
        adminService.rejectPost(id, reason);
        return "redirect:/admin/pending?rejected=true";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return "redirect:/admin/posts";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("users", adminService.getUsers(PageRequest.of(page, 20)));
        model.addAttribute("pendingCount", adminService.countPending());
        return "admin/users";
    }

    @PostMapping("/users/{id}/promote")
    public String promote(@PathVariable Long id) {
        adminService.promoteUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/disable")
    public String disable(@PathVariable Long id) {
        adminService.disableUser(id);
        return "redirect:/admin/users";
    }
}
