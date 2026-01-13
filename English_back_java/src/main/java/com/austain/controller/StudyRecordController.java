package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.dto.RecordResult;
import com.austain.domain.po.RecordRequest;
import com.austain.service.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyRecord")
public class StudyRecordController {

    @Autowired
    private StudyRecordService studyRecordService;


    /**
     * 添加学习记录
     * @param recordRequest 前端传递的参数——请求体
     * @return 添加成功返回成功，失败返回失败
     */
    @PostMapping("/addRecord")
    public Result addStudyRecord(@RequestBody RecordRequest recordRequest) {
        System.out.println("添加记录为：" + (recordRequest != null ? recordRequest.getLearningRecord() : "null") + "，类型：" + (recordRequest != null ? recordRequest.getType() : "null"));
        if (recordRequest == null) {
            return Result.error("请求参数不能为空");
        }
        if (recordRequest.getUserId() <= 0) {
            return Result.error("请先登录后再添加学习清单");
        }
        if (recordRequest.getType() < 0 || recordRequest.getType() > 2) {
            return Result.error("学习类型不合法");
        }
        if (recordRequest.getLearningRecord() == null || recordRequest.getLearningRecord().trim().isEmpty()) {
            return Result.error("请填写学习内容");
        }
        recordRequest.setLearningRecord(recordRequest.getLearningRecord().trim());

        int type = recordRequest.getType();
        if (type == 1) {  // 单词
            if (recordRequest.getBookId() == null || recordRequest.getBookId() <= 0) {
                return Result.error("请选择关联课本");
            }
            if (recordRequest.getStartId() == null || recordRequest.getEndId() == null) {
                return Result.error("请填写单词范围");
            }
            if (recordRequest.getStartId() <= 0 || recordRequest.getEndId() <= 0 || recordRequest.getStartId() >= recordRequest.getEndId()) {
                return Result.error("单词范围不合法");
            }
            if (recordRequest.getRecordIds() == null || recordRequest.getRecordIds().trim().isEmpty()) {
                recordRequest.setRecordIds(recordRequest.getStartId() + "-" + recordRequest.getEndId());
            } else {
                recordRequest.setRecordIds(recordRequest.getRecordIds().trim());
            }
        } else if (type == 0) {  // 句子
            recordRequest.setBookId(999L);
            recordRequest.setStartId(0);
            recordRequest.setEndId(1);
            recordRequest.setRecordIds("I am sentence record");
        } else if (type == 2) {  // 听力
            recordRequest.setBookId(998L);
            recordRequest.setStartId(0);
            recordRequest.setEndId(1);
            recordRequest.setRecordIds("I am listening record");
        }

        recordRequest.setSelected(0);
        recordRequest.setAlreadyReviewed(0);
        int result = studyRecordService.addStudyRecord(recordRequest);
        return result > 0 ? Result.success() : Result.error("添加失败");
    }

    /**
     * 获取今日学习记录
     * @return 今日学习记录
     */
    @GetMapping("/list/{userId}")
    public Result getTodayList(@PathVariable int userId, @RequestParam int type) {
        System.out.println("获取今日学习记录");
        List<RecordResult> studyRecordList = studyRecordService.getTodayList(userId, type);
        if (studyRecordList == null || studyRecordList.isEmpty()) {
            return Result.error("该用户还未添加学习清单~");
        }
        return Result.success(studyRecordList);
    }

    /**
     * 设置学习清单已复习
     */
    @PostMapping("/setReview/{userId}")
    public Result setReview(@PathVariable int userId ,@RequestBody List< Integer > ids){
        System.out.println("设置学习清单已复习");
        int result = studyRecordService.setReview(userId,ids);
        return result > 0 ? Result.success() : Result.error("设置失败");
    }

    /**
     * 更新学习记录,但不标记已掌握
     * @param request 前端传递的参数——请求体
     * @return 更新成功返回成功，失败返回失败
     */
    @PostMapping("/update")
    public Result updateRecord(@RequestBody RecordRequest request){
        System.out.println("更新学习记录");
        int result = studyRecordService.updateRecord(request);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除学习记录
     * @param ids 需要删除的记录ID列表
     * @param userId 用户ID
     * @return 删除成功返回成功，失败返回失败
     */
    @PostMapping("/delete")  // 封装成批量删除，即使删除一个记录，前端传参也得封装成请求体
    public Result deleteRecords(@RequestBody List<String> ids,@RequestParam int userId) {
        System.out.println("删除学习记录");
        int result = studyRecordService.deleteRecords(ids, userId);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/resetSelected")
    public Result resetSelected(@RequestParam int userId){
        System.out.println("重置已选择");
        int result = studyRecordService.resetSelected(userId);
        return result > 0 ? Result.success("刷新成功") : Result.error("刷新数据失败/今日暂无学习数据，可以休息一下~");
    }
}
