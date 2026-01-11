package com.austain.srevice;

import com.austain.domain.dto.*;
import com.austain.domain.po.WordStudyRecord;

import java.util.List;

/**
 * 单词学习服务接口
 */
public interface WordStudyService {
    
    /**
     * 开始学习会话
     */
    WordStudyRecord startStudySession(StudySessionRequest request);
    
    /**
     * 结束学习会话
     */
    WordStudyRecord endStudySession(Long sessionId, StudyEndRequest request);
    
    /**
     * 检查用户复习状态
     */
    StudyLockStatusResponse checkReviewStatus(Long userId);
    
    /**
     * 标记复习完成
     */
    boolean markReviewComplete(ReviewCompleteRequest request);
    
    /**
     * 获取用户今日学习记录
     */
    List<WordStudyRecord> getTodayRecords(Long userId);
    
    /**
     * 检查用户是否有未完成的复习任务
     */
    boolean hasUnfinishedReview(Long userId);

    /**
     * 获取用户最新完成的学习记录
     */
    WordStudyRecord getLatestFinishedRecord(Long userId);

    /**
     * 获取指定课本的最新完成学习记录
     */
    WordStudyRecord getLatestFinishedRecord(Long userId, Long bookId);
}
