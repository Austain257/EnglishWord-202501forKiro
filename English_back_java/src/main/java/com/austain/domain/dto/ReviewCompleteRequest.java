package com.austain.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 复习完成请求
 * @author kiro
 */
@Data
public class ReviewCompleteRequest {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 学习会话ID
     */
    private Long sessionId;
    
    /**
     * 复习轮次：1-8
     */
    private Integer reviewRound;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedTime;
}
