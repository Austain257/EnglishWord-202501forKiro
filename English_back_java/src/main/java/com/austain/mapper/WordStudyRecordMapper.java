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
    @Insert("INSERT INTO word_study_record (user_id, book_id, start_time, start_id, end_time, end_id, status, create_time, update_time) " +
            "VALUES (#{userId}, #{bookId}, #{startTime}, #{startId}, #{endTime}, #{endId}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertStudyRecord(WordStudyRecord record);
    
    /**
     * 更新学习记录
     */
    @Update("UPDATE word_study_record SET end_time = #{endTime}, end_id = #{endId}, status = #{status}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    int updateStudyRecord(WordStudyRecord record);
    
    /**
     * 更新复习完成时间
     */
    @Update("<script>" +
            "UPDATE word_study_record SET " +
            "<if test='reviewRound == 1'>round_1_review_time = #{completedTime}</if>" +
            "<if test='reviewRound == 2'>round_2_review_time = #{completedTime}</if>" +
            "<if test='reviewRound == 3'>round_3_review_time = #{completedTime}</if>" +
            "<if test='reviewRound == 4'>round_4_review_time = #{completedTime}</if>" +
            "<if test='reviewRound == 5'>round_5_review_time = #{completedTime}</if>" +
            "<if test='reviewRound == 6'>round_6_review_time = #{completedTime}</if>" +
            "<if test='reviewRound == 7'>round_7_review_time = #{completedTime}</if>" +
            "<if test='reviewRound == 8'>round_8_review_time = #{completedTime}</if>" +
            ", update_time = NOW() " +
            "WHERE id = #{sessionId}" +
            "</script>")
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
    @Select("SELECT * FROM word_study_record WHERE user_id = #{userId} AND DATE(create_time) = CURDATE() ORDER BY create_time DESC")
    List<WordStudyRecord> selectTodayRecords(Long userId);
    
    /**
     * 查询用户未完成复习的记录
     */
    @Select("SELECT * FROM word_study_record WHERE user_id = #{userId} AND status = 0 " +
            "AND (round_1_review_time IS NULL OR round_2_review_time IS NULL) " +
            "AND DATE(create_time) = CURDATE()")
    List<WordStudyRecord> selectUnfinishedReviews(Long userId);
    
    /**
     * 查询用户是否有进行中的学习会话
     */
    @Select("SELECT * FROM word_study_record WHERE user_id = #{userId} AND status = 1 ORDER BY create_time DESC LIMIT 1")
    WordStudyRecord selectActiveSession(Long userId);

    /**
     * 查询用户最新完成的学习记录
     */
    @Select("SELECT * FROM word_study_record WHERE user_id = #{userId} AND status = 0 ORDER BY create_time DESC LIMIT 1")
    WordStudyRecord selectLatestFinishedRecord(Long userId);

    /**
     * 查询指定课本的最新完成学习记录
     */
    @Select("SELECT * FROM word_study_record WHERE user_id = #{userId} AND book_id = #{bookId} AND status = 0 ORDER BY create_time DESC LIMIT 1")
    WordStudyRecord selectLatestFinishedRecordByBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
