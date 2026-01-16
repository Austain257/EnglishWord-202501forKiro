package com.austain.mapper;

import com.austain.domain.dto.RecordResult;
import com.austain.domain.po.RecordRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudyRecordMapper {

    /**
     * 添加学习记录
     * @return 添加结果
     */
    int addStudyRecord(RecordRequest recordRequest);

    /**
     * 获取今天的学习记录 -- 查询已标记的单词
     * @return 今天的学习记录
     */
    @Select("select * from learning_checklist where user_id = #{userId} and selected = 1 and type = #{type}")
    List<RecordResult> getTodayListSelected(int userId, int type);

    /**
     * 删除指定ID的记录
     * @param ids 记录ID列表
     * @return 删除数量
     */
    int deleteRecords(@Param("ids") List<String> ids, @Param("userId") int userId);

    /**
     * 获取指定用户指定类型的所有学习记录
     * @param userId 用户ID
     * @param type 学习类型
     * @return 学习记录列表
     */
    @Select("select * from learning_checklist where user_id = #{userId} and type = #{type} order by create_time desc")
    List<RecordResult> getListAll(int userId, int type);

    /**
     * 批量更新已标记的记录
     * @param ids 记录ID列表
     */
    void updateSelected(List<Integer> ids);

    /**
     * 重置已标记的记录
     * @param userId 用户ID
     */
    @Update("update learning_checklist set selected = 0 where user_id = #{userId}")  // 校验userId，更新用户自己的
    void setSelected(int userId);

    /**
     * 更新学习记录
     * @param request 更新内容
     * @return 更新数量
     */
    int updateRecord(RecordRequest request);

    /**
     * 重置已标记的记录 -- 定时器使用
     * 重置所有人的已标记的记录
     */
    @Update("update learning_checklist set selected = 0,already_reviewed = 0")
    int resetSelect();

    // 批量更新已复习的记录
    int setReview(List<Integer> ids);

    // 重置已复习的记录
    @Update("update learning_checklist set already_reviewed = 0 where user_id = #{userId}")
    void resetReviewed(int userId);

    // 合并学习记录 - 单词
    void MergeStudyRecordsForWord();

    // 合并学习记录 - 句子
    void MergeStudyRecordsForSentence();


    // 重置指定用户的已标记的记录
    @Update("update learning_checklist set selected = 0, already_reviewed = 0 where user_id = #{userId}")
    int resetSelectByUserId(int userId);

    // 根据用户ID生成学习记录
    int generateRecordByUserId(@Param("userId") int userId, @Param("bookId") int bookId);

    /**
     * 获取指定用户在某个课本下最新的一条单词学习清单
     */
    @Select("select * from learning_checklist where user_id = #{userId} and book_id = #{bookId} and type = 1 order by create_time desc limit 1")
    RecordResult getLatestChecklistByUserAndBook(@Param("userId") int userId, @Param("bookId") Long bookId);
}
