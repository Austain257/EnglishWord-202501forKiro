package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.dto.SentenceStudyRecordRequest;
import com.austain.domain.dto.ReviewCompleteRequest;
import com.austain.domain.dto.RecordsByIdsRequest;
import com.austain.domain.po.SentenceStudyRecord;
import com.austain.service.SentenceStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 句子学习记录接口
 */
@RestController
@RequestMapping("/api/sentence-study")
@CrossOrigin
public class SentenceStudyController {

    @Autowired
    private SentenceStudyService sentenceStudyService;

    /**
     * 保存一句学习记录
     */
    @PostMapping("/record")
    public Result saveRecord(@RequestBody SentenceStudyRecordRequest request) {
        try {
            int rows = sentenceStudyService.saveRecord(request);
            if (rows > 0) {
                return Result.success("记录保存成功");
            }
            return Result.error("保存失败");
        } catch (Exception e) {
            return Result.error("保存句子学习记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取最新完成的句子学习记录（status=0）
     */
    @GetMapping("/latest-record/{userId}")
    public Result getLatestRecord(@PathVariable Long userId,
                                  @RequestParam(value = "bookId", required = false) Long bookId) {
        try {
            SentenceStudyRecord record = sentenceStudyService.getLatestFinishedRecord(userId, bookId);
            if (record == null) {
                return Result.error("未找到学习记录");
            }
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("获取句子学习记录失败：" + e.getMessage());
        }
    }

    /**
     * 标记句子复习完成
     */
    @PostMapping("/review/complete")
    public Result markReviewComplete(@RequestBody ReviewCompleteRequest request) {
        try {
            boolean ok = sentenceStudyService.markReviewComplete(request);
            return ok ? Result.success("标记成功") : Result.error("标记失败");
        } catch (Exception e) {
            return Result.error("标记复习完成失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID列表获取句子学习记录（用于前端渲染轮次状态）
     */
    @PostMapping("/records/batch")
    public Result getRecordsByIds(@RequestBody RecordsByIdsRequest request) {
        try {
            java.util.List<SentenceStudyRecord> records = sentenceStudyService.getRecordsByIds(request.getUserId(), request.getRecordIds());
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取句子学习记录失败：" + e.getMessage());
        }
    }
}
