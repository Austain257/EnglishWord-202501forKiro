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
    @Insert("INSERT INTO user_study_daily (user_id, stat_date, total_sec, updated_at) " +
            "VALUES (#{userId}, #{statDate}, #{totalSec}, #{updatedAt}) " +
            "ON DUPLICATE KEY UPDATE total_sec = total_sec + #{totalSec}, updated_at = #{updatedAt}")
    int upsertDailyStats(UserStudyDaily dailyStats);

    /**
     * 插入或更新累计统计数据
     */
    @Insert("INSERT INTO user_study_summary (user_id, total_sec, updated_at) " +
            "VALUES (#{userId}, #{totalSec}, #{updatedAt}) " +
            "ON DUPLICATE KEY UPDATE total_sec = total_sec + #{totalSec}, updated_at = #{updatedAt}")
    int upsertTotalStats(UserStudySummary totalStats);

    /**
     * 查询用户指定日期的学习时长
     */
    @Select("SELECT * FROM user_study_daily WHERE user_id = #{userId} AND stat_date = #{date}")
    UserStudyDaily findStatsByDate(@Param("userId") Long userId, @Param("date") String date);

    /**
     * 查询用户最近N天的学习数据
     */
    @Select("SELECT * FROM user_study_daily WHERE user_id = #{userId} AND " +
            "stat_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) ORDER BY stat_date DESC")
    java.util.List<UserStudyDaily> findRecentStats(@Param("userId") Long userId, @Param("days") Integer days);

    /**
     * 初始化用户学习统计数据
     */
    @Insert("INSERT IGNORE INTO user_study_summary (user_id, total_sec, updated_at) " +
            "VALUES (#{userId}, 0, NOW())")
    int initUserStats(@Param("userId") Long userId);
}
