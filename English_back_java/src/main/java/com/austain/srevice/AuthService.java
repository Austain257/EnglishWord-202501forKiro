package com.austain.srevice;

import com.austain.domain.po.User;
import com.austain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成token（简单实现，实际应使用JWT）
        String token = UUID.randomUUID().toString();

        // 存储token到TokenService
        tokenService.storeToken(token, user.getId());

        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId());

        // 返回用户信息和token
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return result;
    }

    /**
     * 用户注册
     */
    public void register(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null) {
            User existEmail = userMapper.findByEmail(user.getEmail());
            if (existEmail != null) {
                throw new RuntimeException("邮箱已被使用");
            }
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getStatus() == null) {
            user.setStatus(1L);
        }

        // 保存用户
        userMapper.insert(user);
    }

    /**
     * 刷新token
     */
    public Map<String, Object> refreshToken(String oldToken) {
        // 简单实现：生成新token
        // 实际应用中应该验证旧token的有效性，并从中提取用户信息
        String newToken = UUID.randomUUID().toString();
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        
        return result;
    }
}
