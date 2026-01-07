package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户学习会话实体类
 * 记录每次学习会话的详细信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStudySession {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 当前课本ID
     */
    private Long bookId;
    
    /**
     * 学习场景编码（如 word_review_first）
     */
    private String studyScene;
    
    /**
     * 会话开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 会话结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 会话总秒数
     */
    private Integer durationSec;
    
    /**
     * 状态：0=进行中，1=已结束，2=强制结束
     */
    private Integer status;
    
    /**
     * 最近心跳时间
     */
    private LocalDateTime lastHeartbeat;
    
    /**
     * 暂停时间
     */
    private LocalDateTime pausedAt;
    
    /**
     * 累计暂停秒数
     */
    private Integer pauseDurationSec;
    
    /**
     * 是否暂停中
     */
    private Boolean isPaused;
    
    /**
     * 触发来源（路由/组件/自动）
     */
    private String source;
    
    /**
     * 前端上报细节（JSON格式）
     */
    private String clientMeta;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
