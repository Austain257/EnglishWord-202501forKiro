package com.austain.domain.dto;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

/**
 * 学习锁定状态响应
 * @author kiro
 */
@Data
public class StudyLockStatusResponse {
    /**
     * 是否锁定
     */
    private Boolean isLocked;
    
    /**
     * 锁定原因
     */
    private String reason;
    
    /**
     * 允许的操作列表
     */
    private List<String> allowedActions;
    
    /**
     * 被阻止的操作列表
     */
    private List<String> blockedActions;
    
    /**
     * 提示消息
     */
    private String message;
    
    /**
     * 待完成的复习任务
     */
    private List<PendingReview> pendingReviews;
    
    /**
     * 待完成复习内部类
     */
    @Data
    public static class PendingReview {
        /**
         * 会话ID
         */
        private Long sessionId;
        
        /**
         * 学习范围
         */
        private String range;
        
        /**
         * 待完成的轮次
         */
        private Integer pendingRound;
        
        /**
         * 创建时间
         */
        private LocalDateTime createTime;
    }
}
