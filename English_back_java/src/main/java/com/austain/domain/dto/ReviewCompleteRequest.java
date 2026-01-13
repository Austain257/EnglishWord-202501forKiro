package com.austain.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

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
     * 学习会话ID列表（可选，优先使用）
     */
    private List<Long> sessionIds;

    /**
     * 合并记录的 recordIds 文本（例如 "101,102,103" 或 "101-120"）
     */
    private String recordIdsText;
    
    /**
     * 复习轮次：1-8
     */
    private Integer reviewRound;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedTime;
}
