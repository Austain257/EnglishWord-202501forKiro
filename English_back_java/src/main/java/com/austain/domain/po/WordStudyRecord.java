package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 单词学习记录实体类
 * @author kiro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordStudyRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 课本ID
     */
    private Long bookId;
    
    /**
     * 开始学习时间
     */
    private LocalDateTime startTime;
    
    /**
     * 本轮学习单词范围的起始ID
     */
    private Integer startId;
    
    /**
     * 实际结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 本轮学习单词范围的结束ID
     */
    private Integer endId;
    
    /**
     * 会话状态：1-进行中，0-正常结束，4-异常退出
     */
    private Integer status;
    
    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 记录更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 第一轮复习结束时间
     */
    private LocalDateTime round1ReviewTime;
    
    /**
     * 第二轮复习结束时间
     */
    private LocalDateTime round2ReviewTime;
    
    /**
     * 第三轮复习结束时间
     */
    private LocalDateTime round3ReviewTime;
    
    /**
     * 第四轮复习结束时间
     */
    private LocalDateTime round4ReviewTime;
    
    /**
     * 第五轮复习结束时间
     */
    private LocalDateTime round5ReviewTime;
    
    /**
     * 第六轮复习结束时间
     */
    private LocalDateTime round6ReviewTime;
    
    /**
     * 第七轮复习结束时间
     */
    private LocalDateTime round7ReviewTime;
    
    /**
     * 第八轮复习结束时间
     */
    private LocalDateTime round8ReviewTime;

    /**
     * 第九轮复习结束时间
     */
    private LocalDateTime round9ReviewTime;
}
