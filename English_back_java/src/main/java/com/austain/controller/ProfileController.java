package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.dto.profile.ProfileDashboardDTO;
import com.austain.domain.dto.profile.ProfileDashboardDTO.PasswordUpdate;
import com.austain.service.ProfileService;
import com.austain.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/me")
    public Result getProfile(HttpServletRequest request) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return Result.error("用户未登录或Token无效");
        }
        ProfileDashboardDTO profile = profileService.getProfile(userId);
        return Result.success(profile);
    }

    @PutMapping("/settings")
    public Result updateSettings(@RequestBody ProfileDashboardDTO.Settings settings, HttpServletRequest request) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return Result.error("用户未登录或Token无效");
        }
        try {
            ProfileDashboardDTO.Settings updated = profileService.updateSettings(userId, settings);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    public Result updatePassword(@RequestBody PasswordUpdate passwordUpdate, HttpServletRequest request) {
        Long userId = resolveUserId(request);
        if (userId == null) {
            return Result.error("用户未登录或Token无效");
        }
        try {
            profileService.updatePassword(userId, passwordUpdate);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private Long resolveUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (authHeader != null) {
            token = authHeader;
        }
        if (token == null || token.isEmpty()) {
            token = request.getHeader("X-Token");
        }
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }
        Long userId = tokenService.validateTokenAndGetUserId(token);
        if (userId != null) {
            return userId;
        }
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException ignored) {
            }
        }
        String userIdParam = request.getParameter("userId");
        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                return Long.parseLong(userIdParam);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
