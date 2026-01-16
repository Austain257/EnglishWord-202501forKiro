package com.austain.service;

import com.austain.domain.dto.SentenceStudyRecordRequest;
import com.austain.domain.po.SentenceStudyRecord;
import com.austain.domain.dto.ReviewCompleteRequest;

public interface SentenceStudyService {

    /**
     * 保存一句学习记录
     */
    int saveRecord(SentenceStudyRecordRequest request);

    /**
     * 获取最新完成的句子学习记录（status=0）
     */
    SentenceStudyRecord getLatestFinishedRecord(Long userId, Long bookId);

    /**
     * 标记句子复习完成
     */
    boolean markReviewComplete(ReviewCompleteRequest request);

    /**
     * 根据ID列表查询记录（可校验用户归属）
     */
    java.util.List<com.austain.domain.po.SentenceStudyRecord> getRecordsByIds(Long userId, java.util.List<Long> recordIds);
}
