package com.austain.mapper;

import com.austain.domain.po.SentenceStudyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SentenceStudyRecordMapper {

    int insertRecord(SentenceStudyRecord record);

    int updateReviewTime(@Param("sessionId") Long sessionId,
                         @Param("reviewRound") Integer reviewRound,
                         @Param("completedTime") java.time.LocalDateTime completedTime);

    java.util.List<SentenceStudyRecord> selectByIds(@Param("ids") java.util.List<Long> ids);

    /**
     * 查询用户最新完成的句子学习记录（status=0）
     */
    SentenceStudyRecord selectLatestFinishedRecord(@Param("userId") Long userId);

    /**
     * 查询指定书本的最新完成句子学习记录（status=0）
     */
    SentenceStudyRecord selectLatestFinishedRecordByBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
