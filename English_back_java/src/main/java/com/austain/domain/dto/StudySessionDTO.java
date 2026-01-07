package com.austain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学习会话请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionDTO {
    
    /**
     * 学习场景编码
     */
    private String studyScene;
    
    /**
     * 会话ID（用于心跳和结束会话）
     */
    private Long sessionId;
    
    /**
     * 结束时间（可选）
     */
    private LocalDateTime endTime;
    
    /**
     * 客户端计算的时长秒数（可选）
     */
    private Integer clientDurationSec;
    
    /**
     * 触发来源
     */
    private String source;
    
    /**
     * 客户端元数据
     */
    private String clientMeta;
}
