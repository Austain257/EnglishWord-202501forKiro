package com.austain.srevice;

import com.austain.domain.dto.StudyLockStatusResponse;
import java.util.List;

/**
 * 学习锁定服务接口
 * @author kiro
 */
public interface StudyLockService {
    
    /**
     * 检查功能锁定状态
     */
    StudyLockStatusResponse checkLockStatus(Long userId);
    
    /**
     * 获取待完成复习任务
     */
    List<StudyLockStatusResponse.PendingReview> getPendingReviews(Long userId);
    
    /**
     * 检查是否允许访问指定功能
     */
    boolean isActionAllowed(Long userId, String action);
}
