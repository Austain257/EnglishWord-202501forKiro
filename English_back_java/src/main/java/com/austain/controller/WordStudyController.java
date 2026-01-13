package com.austain.controller;

import com.austain.domain.dto.*;
import com.austain.domain.po.WordStudyRecord;
import com.austain.service.WordStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单词学习记录控制器
 */
@RestController
@RequestMapping("/api/word-study")
@CrossOrigin
public class WordStudyController {
    
    @Autowired
    private WordStudyService wordStudyService;
    
    /**
     * 开始学习会话
     */
    @PostMapping("/start")
    public Result startStudySession(@RequestBody StudySessionRequest request) {
        System.out.println("开始学习会话已触发");
        try {
            WordStudyRecord session = wordStudyService.startStudySession(request);
            return Result.success(session);
        } catch (Exception e) {
            return Result.error("开始学习会话失败：" + e.getMessage());
        }
    }
    
    /**
     * 结束学习会话
     */
    @PostMapping("/end/{sessionId}")
    public Result endStudySession(@PathVariable Long sessionId, @RequestBody StudyEndRequest request) {
        System.out.println("结束学习会话已触发，会话ID：" + sessionId);
        try {
            WordStudyRecord session = wordStudyService.endStudySession(sessionId, request);
            return Result.success(session);
        } catch (Exception e) {
            return Result.error("结束学习会话失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查用户复习状态
     */
    @GetMapping("/review-status/{userId}")
    public Result checkReviewStatus(@PathVariable Long userId) {
        System.out.println("检查复习状态已触发，用户ID：" + userId);
        try {
            StudyLockStatusResponse status = wordStudyService.checkReviewStatus(userId);
            return Result.success(status);
        } catch (Exception e) {
            return Result.error("检查复习状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 标记复习完成
     */
    @PostMapping("/review/complete")
    public Result markReviewComplete(@RequestBody ReviewCompleteRequest request) {
        System.out.println("标记复习完成已触发，轮次：" + request.getReviewRound());
        try {
            boolean success = wordStudyService.markReviewComplete(request);
            if (success) {
                return Result.success("第" + request.getReviewRound() + "轮复习已完成");
            } else {
                return Result.error("标记复习完成失败");
            }
        } catch (Exception e) {
            return Result.error("标记复习完成失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID列表获取学习记录
     */
    @PostMapping("/records/batch")
    public Result getRecordsByIds(@RequestBody RecordsByIdsRequest request) {
        try {
            List<WordStudyRecord> records = wordStudyService.getRecordsByIds(request.getUserId(), request.getRecordIds());
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取学习记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户今日学习记录
     */
    @GetMapping("/today-records/{userId}")
    public Result getTodayRecords(@PathVariable Long userId) {
        System.out.println("获取今日学习记录已触发，用户ID：" + userId);
        try {
            List<WordStudyRecord> records = wordStudyService.getTodayRecords(userId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取今日学习记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户最新完成的学习记录
     */
    @GetMapping("/latest-record/{userId}")
    public Result getLatestFinishedRecord(@PathVariable Long userId,
                                          @RequestParam(value = "bookId", required = false) Long bookId) {
        System.out.println("获取最新完成学习记录，用户ID：" + userId);
        try {
            WordStudyRecord record = wordStudyService.getLatestFinishedRecord(userId, bookId);
            if (record == null) {
                return Result.error("未找到学习记录");
            }
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("获取最新学习记录失败：" + e.getMessage());
        }
    }
}
