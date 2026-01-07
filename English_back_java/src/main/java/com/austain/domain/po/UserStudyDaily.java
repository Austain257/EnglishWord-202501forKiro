package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户每日学习时长统计实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStudyDaily {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 统计日期
     */
    private LocalDate statDate;
    
    /**
     * 当日累计秒数
     */
    private Integer totalSec;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
}
