package com.austain.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 结束学习会话请求
 * @author kiro
 */
@Data
public class StudyEndRequest {
    /**
     * 课本ID
     */
    private Long bookId;
    
    /**
     * 结束单词ID
     */
    private Integer endId;
    
    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;
}
