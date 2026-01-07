package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户学习总时长统计实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStudySummary {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 累计学习秒数
     */
    private Long totalSec;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
}
