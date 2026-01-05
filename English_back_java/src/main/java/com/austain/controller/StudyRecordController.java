package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.dto.RecordResult;
import com.austain.domain.po.RecordRequest;
import com.austain.srevice.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        System.out.println("添加记录为：" + recordRequest.getLearningRecord() + "，类型：" + recordRequest.getType());
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
}
