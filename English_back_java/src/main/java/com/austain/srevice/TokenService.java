package com.austain.srevice;

import com.austain.domain.po.User;
import com.austain.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Token管理服务
 * 简单的内存Token管理，实际生产环境建议使用Redis
 */
@Service
public class TokenService {

    @Autowired
    private UserMapper userMapper;

    // 存储Token到用户ID的映射
    private final ConcurrentHashMap<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();
    
    // 定时清理过期Token
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TokenService() {
        // 每小时清理一次过期Token
        scheduler.scheduleAtFixedRate(this::cleanExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    /**
     * 存储Token
     */
    public void storeToken(String token, Long userId) {
        tokenStore.put(token, new TokenInfo(userId, System.currentTimeMillis()));
    }

    /**
     * 根据Token获取用户ID
     */
    public Long getUserIdByToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null) {
            return null;
        }
        
        // 检查Token是否过期（24小时）
        long expireTime = 24 * 60 * 60 * 1000; // 24小时
        if (System.currentTimeMillis() - tokenInfo.getCreateTime() > expireTime) {
            tokenStore.remove(token);
            return null;
        }
        
        return tokenInfo.getUserId();
    }

    /**
     * 根据Token获取用户ID（用于Controller中的认证）
     */
    public Long validateTokenAndGetUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return getUserIdByToken(token);
    }

    /**
     * 移除Token
     */
    public void removeToken(String token) {
        tokenStore.remove(token);
    }

    /**
     * 验证Token是否有效
     */
    public boolean isValidToken(String token) {
        return getUserIdByToken(token) != null;
    }

    /**
     * 清理过期Token
     */
    private void cleanExpiredTokens() {
        long expireTime = 24 * 60 * 60 * 1000; // 24小时
        long currentTime = System.currentTimeMillis();
        
        tokenStore.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().getCreateTime() > expireTime
        );
    }

    /**
     * Token信息内部类
     */
    private static class TokenInfo {
        private final Long userId;
        private final long createTime;

        public TokenInfo(Long userId, long createTime) {
            this.userId = userId;
            this.createTime = createTime;
        }

        public Long getUserId() {
            return userId;
        }

        public long getCreateTime() {
            return createTime;
        }
    }
}
