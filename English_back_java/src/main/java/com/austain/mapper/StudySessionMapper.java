package com.austain.mapper;

import com.austain.domain.po.UserStudySession;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习会话数据访问接口
 */
@Mapper
public interface StudySessionMapper {

    /**
     * 插入新的学习会话
     */
    @Insert("INSERT INTO user_study_session (user_id, book_id, study_scene, start_time, status, last_heartbeat, source, client_meta, created_at, updated_at) " +
            "VALUES (#{userId}, #{bookId}, #{studyScene}, #{startTime}, #{status}, #{lastHeartbeat}, #{source}, #{clientMeta}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSession(UserStudySession session);

    /**
     * 更新会话结束信息
     */
    @Update("UPDATE user_study_session SET end_time = #{endTime}, duration_sec = #{durationSec}, pause_duration_sec = #{pauseDurationSec}, " +
            "is_paused = 0, paused_at = NULL, status = #{status}, updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int finishSession(@Param("id") Long sessionId,
                      @Param("userId") Long userId,
                      @Param("endTime") LocalDateTime endTime,
                      @Param("durationSec") Integer durationSec,
                      @Param("status") Integer status,
                      @Param("pauseDurationSec") Integer pauseDurationSec,
                      @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新心跳时间
     */
    @Update("UPDATE user_study_session SET last_heartbeat = #{heartbeatTime}, updated_at = #{updatedAt} " +
            "WHERE id = #{sessionId} AND user_id = #{userId} AND status = 0")
    int updateHeartbeat(@Param("sessionId") Long sessionId, 
                       @Param("userId") Long userId,
                       @Param("heartbeatTime") LocalDateTime heartbeatTime,
                       @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查找用户当前运行中的会话
     */
    @Select("SELECT * FROM user_study_session WHERE user_id = #{userId} AND status = 0 ORDER BY start_time DESC LIMIT 1")
    UserStudySession findRunningSession(@Param("userId") Long userId);

    /**
     * 根据会话ID和用户ID查询会话
     */
    @Select({"<script>",
            "SELECT * FROM user_study_session WHERE id = #{sessionId}",
            "<if test='userId != null'> AND user_id = #{userId}</if>",
            "</script>"})
    UserStudySession findSessionById(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 强制结束超时会话
     */
    @Update("UPDATE user_study_session SET end_time = #{endTime}, duration_sec = #{durationSec}, pause_duration_sec = #{pauseDurationSec}, " +
            "is_paused = 0, paused_at = NULL, status = 2, updated_at = #{updatedAt} WHERE id = #{sessionId}")
    int forceFinishSession(@Param("sessionId") Long sessionId,
                           @Param("endTime") LocalDateTime endTime,
                           @Param("durationSec") Integer durationSec,
                           @Param("pauseDurationSec") Integer pauseDurationSec,
                           @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查找超时的运行中会话
     */
    @Select("SELECT * FROM user_study_session WHERE status = 0 AND is_paused = 0 AND " +
            "TIMESTAMPDIFF(MINUTE, last_heartbeat, #{currentTime}) > #{timeoutMinutes}")
    List<UserStudySession> findTimeoutSessions(@Param("currentTime") LocalDateTime currentTime,
                                              @Param("timeoutMinutes") Integer timeoutMinutes);

    /**
     * 查找暂停超时的会话
     */
    @Select("SELECT * FROM user_study_session WHERE status = 0 AND is_paused = 1 AND paused_at IS NOT NULL AND " +
            "TIMESTAMPDIFF(MINUTE, paused_at, #{currentTime}) > #{pausedTimeoutMinutes}")
    List<UserStudySession> findPausedTimeoutSessions(@Param("currentTime") LocalDateTime currentTime,
                                                     @Param("pausedTimeoutMinutes") Integer pausedTimeoutMinutes);

    /**
     * 暂停会话
     */
    @Update("UPDATE user_study_session SET is_paused = 1, paused_at = #{pausedAt}, updated_at = #{updatedAt} " +
            "WHERE id = #{sessionId} AND user_id = #{userId} AND status = 0")
    int pauseSession(@Param("sessionId") Long sessionId,
                    @Param("userId") Long userId,
                    @Param("pausedAt") LocalDateTime pausedAt,
                    @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 恢复会话
     */
    @Update("UPDATE user_study_session SET is_paused = 0, paused_at = NULL, " +
            "pause_duration_sec = IFNULL(pause_duration_sec, 0) + #{pauseDuration}, " +
            "last_heartbeat = #{resumeTime}, updated_at = #{updatedAt} " +
            "WHERE id = #{sessionId} AND user_id = #{userId} AND status = 0")
    int resumeSession(@Param("sessionId") Long sessionId,
                     @Param("userId") Long userId,
                     @Param("pauseDuration") Integer pauseDuration,
                     @Param("resumeTime") LocalDateTime resumeTime,
                     @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查询用户指定日期的学习会话
     */
    @Select("SELECT * FROM user_study_session WHERE user_id = #{userId} AND " +
            "DATE(start_time) = #{date} AND status IN (1, 2) ORDER BY start_time")
    List<UserStudySession> findSessionsByUserAndDate(@Param("userId") Long userId, 
                                                    @Param("date") String date);
}
