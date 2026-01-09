package com.austain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WordStudyRecordMapper {

    @Select("SELECT COUNT(*) FROM word_study_record WHERE user_id = #{userId} AND status = 0")
    int countCompletedSessions(@Param("userId") Long userId);
}
