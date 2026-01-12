package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.dto.StudyLockStatusResponse;
import com.austain.service.StudyLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习锁定状态控制器
 * @author kiro
 */
@RestController
@RequestMapping("/api/study-lock")
@CrossOrigin
public class StudyLockController {
    
    @Autowired
    private StudyLockService studyLockService;
    
    /**
     * 检查功能锁定状态
     */
    @GetMapping("/check/{userId}")
    public Result checkLockStatus(@PathVariable Long userId) {
        System.out.println("检查功能锁定状态已触发，用户ID：" + userId);
        try {
            StudyLockStatusResponse status = studyLockService.checkLockStatus(userId);
            return Result.success(status);
        } catch (Exception e) {
            return Result.error("检查锁定状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取待完成复习任务
     */
    @GetMapping("/pending-reviews/{userId}")
    public Result getPendingReviews(@PathVariable Long userId) {
        System.out.println("获取待完成复习任务已触发，用户ID：" + userId);
        try {
            List<StudyLockStatusResponse.PendingReview> reviews = studyLockService.getPendingReviews(userId);
            return Result.success(reviews);
        } catch (Exception e) {
            return Result.error("获取待完成复习任务失败：" + e.getMessage());
        }
    }
}
