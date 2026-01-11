package com.austain.domain.dto;

import lombok.Data;

/**
 * 开始学习会话请求
 * @author kiro
 */
@Data
public class StudySessionRequest {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 课本ID
     */
    private Long bookId;
    
    /**
     * 开始单词ID
     */
    private Integer startId;
    
    /**
     * 学习时长，单位秒，默认1800（30分钟）
     */
    private Integer studyDuration;
}
