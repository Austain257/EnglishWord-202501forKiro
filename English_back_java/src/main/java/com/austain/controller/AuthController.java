package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.po.User;
import com.austain.srevice.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody User loginRequest) {
        try {
            Map<String, Object> result = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User registerRequest) {
        try {
            authService.register(registerRequest);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result logout() {
        // 简单实现，前端清除token即可
        return Result.success("登出成功");
    }

    /**
     * 验证token
     */
    @GetMapping("/validate")
    public Result validateToken() {
        // 简单实现，如果能访问到这个接口说明token有效
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        return Result.success(result);
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public Result refreshToken(@RequestBody Map<String, String> request) {
        try {
            String oldToken = request.get("token");
            if (oldToken == null || oldToken.isEmpty()) {
                return Result.error("token不能为空");
            }
            // 简单实现：生成新的token
            // 实际项目中应该验证旧token并生成新token
            Map<String, Object> result = authService.refreshToken(oldToken);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("刷新token失败: " + e.getMessage());
        }
    }
}
