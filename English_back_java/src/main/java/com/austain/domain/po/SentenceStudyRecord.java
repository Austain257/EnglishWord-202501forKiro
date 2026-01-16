package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 句子学习记录实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentenceStudyRecord {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime startTime;
    private Integer startId;
    private LocalDateTime endTime;
    private Integer endId;
    /**
     * 会话状态：1-进行中，0-正常结束，4-异常退出
     */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime round1ReviewTime;
    private LocalDateTime round2ReviewTime;
    private LocalDateTime round3ReviewTime;
    private LocalDateTime round4ReviewTime;
    private LocalDateTime round5ReviewTime;
    private LocalDateTime round6ReviewTime;
    private LocalDateTime round7ReviewTime;
    private LocalDateTime round8ReviewTime;
    private LocalDateTime round9ReviewTime;
}
