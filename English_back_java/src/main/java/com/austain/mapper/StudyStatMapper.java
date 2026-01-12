package com.austain.mapper;

import com.austain.domain.po.UserStudyDaily;
import com.austain.domain.po.UserStudySummary;
import org.apache.ibatis.annotations.*;

/**
 * 学习统计数据访问接口
 */
@Mapper
public interface StudyStatMapper {

    /**
     * 查询用户今日学习时长
     */
    @Select("SELECT * FROM user_study_daily WHERE user_id = #{userId} AND stat_date = CURDATE()")
    UserStudyDaily findTodayStats(@Param("userId") Long userId);

    /**
     * 查询用户累计学习时长
     */
    @Select("SELECT * FROM user_study_summary WHERE user_id = #{userId}")
    UserStudySummary findTotalStats(@Param("userId") Long userId);

    /**
     * 插入或更新每日统计数据
     */
    int upsertDailyStats(UserStudyDaily dailyStats);

    /**
     * 插入或更新累计统计数据
     */
    int upsertTotalStats(UserStudySummary totalStats);

    /**
     * 查询用户指定日期的学习时长
     */
    @Select("SELECT * FROM user_study_daily WHERE user_id = #{userId} AND stat_date = #{date}")
    UserStudyDaily findStatsByDate(@Param("userId") Long userId, @Param("date") String date);

    /**
     * 查询用户最近N天的学习数据
     */
    java.util.List<UserStudyDaily> findRecentStats(@Param("userId") Long userId, @Param("days") Integer days);

    /**
     * 统计用户累计学习天数（按有学习时长的日期计数）
     */
    int countStudyDays(@Param("userId") int userId);

    /**
     * 初始化用户学习统计数据
     */
    int initUserStats(@Param("userId") Long userId);
}
