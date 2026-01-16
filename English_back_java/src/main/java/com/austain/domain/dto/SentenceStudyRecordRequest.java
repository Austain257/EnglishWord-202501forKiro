package com.austain.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SentenceStudyRecordRequest {
    private Long userId;
    private Long bookId;
    private Integer startId;
    private Integer endId;
    /**
     * 会话状态：1-进行中，0-正常结束，4-异常退出
     */
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
