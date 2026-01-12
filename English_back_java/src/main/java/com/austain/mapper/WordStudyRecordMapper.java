package com.austain.mapper;

import com.austain.domain.po.WordStudyRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 单词学习记录Mapper
 */
@Mapper
public interface WordStudyRecordMapper {
    
    @Select("SELECT COUNT(*) FROM word_study_record WHERE user_id = #{userId} AND status = 0")
    int countCompletedSessions(@Param("userId") Long userId);
    
    /**
     * 插入学习记录
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertStudyRecord(WordStudyRecord record);
    
    /**
     * 更新学习记录
     */
    int updateStudyRecord(WordStudyRecord record);
    
    /**
     * 更新复习完成时间
     */
    int updateReviewTime(@Param("sessionId") Long sessionId, 
                        @Param("reviewRound") Integer reviewRound, 
                        @Param("completedTime") LocalDateTime completedTime);
    
    /**
     * 根据ID查询学习记录
     */
    @Select("SELECT * FROM word_study_record WHERE id = #{id}")
    WordStudyRecord selectById(Long id);
    
    /**
     * 查询用户今日学习记录
     */
    List<WordStudyRecord> selectTodayRecords(Long userId);
    
    /**
     * 查询用户未完成复习的记录
     */
    List<WordStudyRecord> selectUnfinishedReviews(Long userId);
    
    /**
     * 查询用户是否有进行中的学习会话
     */
    WordStudyRecord selectActiveSession(Long userId);

    /**
     * 查询用户最新完成的学习记录
     */
    WordStudyRecord selectLatestFinishedRecord(Long userId);

    /**
     * 查询指定课本的最新完成学习记录
     */
    WordStudyRecord selectLatestFinishedRecordByBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
