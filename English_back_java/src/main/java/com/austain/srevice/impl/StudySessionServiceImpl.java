package com.austain.srevice.impl;

import com.austain.domain.dto.StudySessionDTO;
import com.austain.domain.dto.StudyStatVO;
import com.austain.domain.po.UserStudyDaily;
import com.austain.domain.po.UserStudySession;
import com.austain.domain.po.UserStudySummary;
import com.austain.mapper.StudySessionMapper;
import com.austain.mapper.StudyStatMapper;
import com.austain.srevice.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 学习会话服务实现类
 */
@Service
public class StudySessionServiceImpl implements StudySessionService {

    @Autowired
    private StudySessionMapper studySessionMapper;

    @Autowired
    private StudyStatMapper studyStatMapper;

    @Override
    @Transactional
    public StudyStatVO startSession(Long userId, Long bookId, String studyScene) {
        // 检查是否有运行中的会话，如果有则先强制结束
        UserStudySession runningSession = studySessionMapper.findRunningSession(userId);
        if (runningSession != null) {
            forceFinishSession(runningSession.getId());
        }

        // 创建新会话
        UserStudySession newSession = new UserStudySession();
        newSession.setUserId(userId);
        newSession.setBookId(bookId);
        newSession.setStudyScene(studyScene);
        newSession.setStartTime(LocalDateTime.now());
        newSession.setStatus(0); // 运行中
        newSession.setLastHeartbeat(LocalDateTime.now());
        newSession.setIsPaused(false);
        newSession.setPausedAt(null);
        newSession.setPauseDurationSec(0);
        newSession.setSource("ROUTE");
        newSession.setCreatedAt(LocalDateTime.now());
        newSession.setUpdatedAt(LocalDateTime.now());

        studySessionMapper.insertSession(newSession);

        // 初始化用户统计数据（如果不存在）
        studyStatMapper.initUserStats(userId);

        return new StudyStatVO(newSession.getId(), LocalDateTime.now());
    }

    @Override
    public boolean heartbeat(Long userId, Long sessionId) {
        LocalDateTime now = LocalDateTime.now();
        int result = studySessionMapper.updateHeartbeat(sessionId, userId, now, now);
        return result > 0;
    }

    @Override
    @Transactional
    public StudyStatVO finishSession(Long userId, StudySessionDTO sessionDTO) {
        // 查找会话
        UserStudySession session = studySessionMapper.findSessionById(sessionDTO.getSessionId(), userId);
        if (session == null || session.getStatus() != 0) {
            throw new RuntimeException("会话不存在或已结束");
        }

        LocalDateTime endTime = sessionDTO.getEndTime() != null ? sessionDTO.getEndTime() : LocalDateTime.now();
        Integer recordedPause = session.getPauseDurationSec() != null ? session.getPauseDurationSec() : 0;
        LocalDateTime lastPausedAt = session.getPausedAt();

        // 如果当前仍处于暂停状态，需要把暂停时长累加并调整结束时间
        if (Boolean.TRUE.equals(session.getIsPaused()) && lastPausedAt != null && lastPausedAt.isBefore(endTime)) {
            recordedPause += calculateDuration(lastPausedAt, endTime);
            // 当用户处于暂停中时，以暂停瞬间作为结束时间，避免把挂机时间算入学习
            endTime = lastPausedAt;
        }
        
        // 计算时长：取客户端时长和服务端计算时长的最大值
        Integer serverDuration = calculateDuration(session.getStartTime(), endTime);
        Integer clientDuration = sessionDTO.getClientDurationSec();
        Integer finalDuration = Math.max(
            serverDuration,
            clientDuration != null ? clientDuration : 0
        );

        int effectiveDuration = Math.max(finalDuration - recordedPause, 0);

        // 防抖：过滤掉少于15秒的会话
        if (effectiveDuration < 15) {
            // 直接结束会话但不计入统计
            studySessionMapper.finishSession(
                sessionDTO.getSessionId(), 
                userId, 
                endTime, 
                0, // 时长设为0
                1, // 正常结束
                recordedPause,
                LocalDateTime.now()
            );
        } else {
            // 正常结束会话
            studySessionMapper.finishSession(
                sessionDTO.getSessionId(), 
                userId, 
                endTime,
                finalDuration,
                1, // 正常结束
                recordedPause,
                LocalDateTime.now()
            );

            // 更新统计数据
            updateStatistics(userId, effectiveDuration, endTime);
        }

        // 返回最新统计数据
        return getTodayStats(userId);
    }

    @Override
    @Transactional
    public boolean forceFinishSession(Long sessionId) {
        // 查找会话
        UserStudySession session = studySessionMapper.findSessionById(sessionId, null);
        if (session == null || session.getStatus() != 0) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now;
        Integer duration = calculateDuration(session.getStartTime(), now);
        Integer pauseDuration = session.getPauseDurationSec() != null ? session.getPauseDurationSec() : 0;

        // 如果会话处于暂停状态，结束时间以 paused_at 为准，并把当前暂停计入
        if (Boolean.TRUE.equals(session.getIsPaused()) && session.getPausedAt() != null) {
            if (session.getPausedAt().isBefore(now)) {
                pauseDuration += calculateDuration(session.getPausedAt(), now);
            }
            endTime = session.getPausedAt();
            duration = calculateDuration(session.getStartTime(), endTime);
        }

        // 强制结束
        int result = studySessionMapper.forceFinishSession(sessionId, endTime, duration, pauseDuration, now);
        
        // 如果时长大于15秒则计入统计
        int effectiveDuration = Math.max(duration - pauseDuration, 0);
        if (result > 0 && effectiveDuration >= 15) {
            updateStatistics(session.getUserId(), effectiveDuration, endTime);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public int autoCloseTimeoutSessions(Integer timeoutMinutes) {
        LocalDateTime now = LocalDateTime.now();
        List<UserStudySession> timeoutSessions = studySessionMapper.findTimeoutSessions(now, timeoutMinutes);
        List<UserStudySession> pausedTimeoutSessions = studySessionMapper.findPausedTimeoutSessions(now, 120); // 2小时
        
        int closedCount = 0;
        for (UserStudySession session : timeoutSessions) {
            if (forceFinishSession(session.getId())) {
                closedCount++;
            }
        }

        for (UserStudySession session : pausedTimeoutSessions) {
            if (forceFinishSession(session.getId())) {
                closedCount++;
            }
        }

        return closedCount;
    }

    @Override
    @Transactional
    public boolean pauseSession(Long userId, Long sessionId) {
        // 查找运行中的会话
        UserStudySession session = studySessionMapper.findSessionById(sessionId, userId);
        if (session == null || session.getStatus() != 0) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        
        // 更新会话为暂停状态
        int result = studySessionMapper.pauseSession(sessionId, userId, now, now);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean resumeSession(Long userId, Long sessionId) {
        // 查找暂停中的会话
        UserStudySession session = studySessionMapper.findSessionById(sessionId, userId);
        if (session == null || session.getStatus() != 0) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        
        // 如果会话确实在暂停状态，计算暂停时长并恢复
        if (session.getPausedAt() != null) {
            // 计算本次暂停时长
            Integer pauseDuration = calculateDuration(session.getPausedAt(), now);
            
            // 更新会话：恢复运行，累加暂停时长
            int result = studySessionMapper.resumeSession(sessionId, userId, pauseDuration, now, now);
            return result > 0;
        }
        
        // 会话没在暂停，直接更新心跳
        return heartbeat(userId, sessionId);
    }

    @Override
    public StudyStatVO getTodayStats(Long userId) {
        // 查询今日统计
        UserStudyDaily todayStats = studyStatMapper.findTodayStats(userId);
        Integer dayTotalSec = todayStats != null ? todayStats.getTotalSec() : 0;

        // 查询累计统计
        UserStudySummary totalStats = studyStatMapper.findTotalStats(userId);
        Long totalSec = totalStats != null ? totalStats.getTotalSec() : 0L;

        return new StudyStatVO(dayTotalSec, totalSec);
    }

    /**
     * 计算时长（秒）
     */
    private Integer calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return Math.max(0, (int) ChronoUnit.SECONDS.between(startTime, endTime));
    }

    /**
     * 更新统计数据
     */
    private void updateStatistics(Long userId, Integer durationSec, LocalDateTime updateTime) {
        // 更新每日统计
        UserStudyDaily dailyStats = new UserStudyDaily();
        dailyStats.setUserId(userId);
        dailyStats.setStatDate(updateTime.toLocalDate());
        dailyStats.setTotalSec(durationSec);
        dailyStats.setUpdatedAt(updateTime);
        studyStatMapper.upsertDailyStats(dailyStats);

        // 更新累计统计
        UserStudySummary totalStats = new UserStudySummary();
        totalStats.setUserId(userId);
        totalStats.setTotalSec(Long.valueOf(durationSec));
        totalStats.setUpdatedAt(updateTime);
        studyStatMapper.upsertTotalStats(totalStats);
    }
}
