package com.austain.service.impl;

import com.austain.domain.dto.StudyLockStatusResponse;
import com.austain.service.StudyLockService;
import com.austain.service.WordStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学习锁定服务实现类
 * @author kiro
 */
@Service
public class StudyLockServiceImpl implements StudyLockService {
    
    @Autowired
    private WordStudyService wordStudyService;
    
    @Override
    public StudyLockStatusResponse checkLockStatus(Long userId) {
        return wordStudyService.checkReviewStatus(userId);
    }
    
    @Override
    public List<StudyLockStatusResponse.PendingReview> getPendingReviews(Long userId) {
        StudyLockStatusResponse status = wordStudyService.checkReviewStatus(userId);
        return status.getPendingReviews();
    }
    
    @Override
    public boolean isActionAllowed(Long userId, String action) {
        StudyLockStatusResponse status = checkLockStatus(userId);
        
        if (!status.getIsLocked()) {
            return true;
        }
        
        List<String> allowedActions = status.getAllowedActions();
        return allowedActions != null && allowedActions.contains(action);
    }
}
