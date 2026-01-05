package com.austain.srevice;

import com.austain.domain.dto.RecordResult;
import com.austain.domain.po.RecordRequest;

import java.util.List;

public interface StudyRecordService {

    /**
     * 添加学习记录
     * @return 添加成功返回1，添加失败返回0
     */
    int addStudyRecord(RecordRequest recordRequest);

    /**
     * 获取今日学习记录
     * @return 今日学习记录列表
     */
    List<RecordResult> getTodayList(int userId, int type);

    /**
     * 删除学习记录
     * @param ids 需要删除的记录ID列表
     * @return 删除成功返回1，失败返回0
     */
    int deleteRecords(List<String> ids, int userId);

    /**
     * 更新学习记录
     * @param request 更新内容
     * @return 更新成功返回1，失败返回0
     */
    int updateRecord(RecordRequest request);

    int setReview(int userId, List< Integer> ids);
}
