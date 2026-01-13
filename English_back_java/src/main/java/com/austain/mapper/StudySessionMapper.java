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
    int insertSession(UserStudySession session);

    /**
     * 更新会话结束信息
     */
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
    int updateHeartbeat(@Param("sessionId") Long sessionId,
                        @Param("userId") Long userId,
                        @Param("heartbeatTime") LocalDateTime heartbeatTime,
                        @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查找用户当前运行中的会话
     */
    UserStudySession findRunningSession(@Param("userId") Long userId);

    /**
     * 根据会话ID和用户ID查询会话
     */
    UserStudySession findSessionById(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 强制结束超时会话
     */
    int forceFinishSession(@Param("sessionId") Long sessionId,
                           @Param("endTime") LocalDateTime endTime,
                           @Param("durationSec") Integer durationSec,
                           @Param("pauseDurationSec") Integer pauseDurationSec,
                           @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查找超时的运行中会话
     */
    List<UserStudySession> findTimeoutSessions(@Param("currentTime") LocalDateTime currentTime,
                                               @Param("timeoutMinutes") Integer timeoutMinutes);

    /**
     * 查找暂停超时的会话
     */
    List<UserStudySession> findPausedTimeoutSessions(@Param("currentTime") LocalDateTime currentTime,
                                                     @Param("pausedTimeoutMinutes") Integer pausedTimeoutMinutes);

    /**
     * 暂停会话
     */
    int pauseSession(@Param("sessionId") Long sessionId,
                     @Param("userId") Long userId,
                     @Param("pausedAt") LocalDateTime pausedAt,
                     @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 恢复会话
     */
    int resumeSession(@Param("sessionId") Long sessionId,
                      @Param("userId") Long userId,
                      @Param("pauseDuration") Integer pauseDuration,
                      @Param("resumeTime") LocalDateTime resumeTime,
                      @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 查询用户指定日期的学习会话
     */
    List<UserStudySession> findSessionsByUserAndDate(@Param("userId") Long userId,
                                                     @Param("date") String date);
}
