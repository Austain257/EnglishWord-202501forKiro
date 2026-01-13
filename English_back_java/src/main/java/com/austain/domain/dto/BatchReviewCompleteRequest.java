package com.austain.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 批量复习完成请求
 */
@Data
public class BatchReviewCompleteRequest {
    private Long userId;
    private List<Long> recordIds;
    private Integer reviewRound;
    private LocalDateTime completedTime;
}
