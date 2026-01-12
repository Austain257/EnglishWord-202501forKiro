package com.austain.service;

import com.austain.domain.dto.StudySessionDTO;
import com.austain.domain.dto.StudyStatVO;

/**
 * 学习会话服务接口
 */
public interface StudySessionService {

    /**
     * 开始学习会话
     * @param userId 用户ID
     * @param bookId 课本ID
     * @param studyScene 学习场景
     * @return 会话信息
     */
    StudyStatVO startSession(Long userId, Long bookId, String studyScene);

    /**
     * 发送心跳，更新会话活跃状态
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean heartbeat(Long userId, Long sessionId);

    /**
     * 结束学习会话
     * @param userId 用户ID
     * @param sessionDTO 会话结束请求
     * @return 更新后的统计数据
     */
    StudyStatVO finishSession(Long userId, StudySessionDTO sessionDTO);

    /**
     * 强制结束会话（用于超时处理）
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean forceFinishSession(Long sessionId);

    /**
     * 自动关闭超时会话
     * @param timeoutMinutes 超时分钟数
     * @return 关闭的会话数量
     */
    int autoCloseTimeoutSessions(Integer timeoutMinutes);

    /**
     * 暂停学习会话（页面后台时）
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean pauseSession(Long userId, Long sessionId);

    /**
     * 恢复学习会话（页面前台时）
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean resumeSession(Long userId, Long sessionId);

    /**
     * 获取用户当前学习统计数据
     * @param userId 用户ID
     * @return 统计数据
     */
    StudyStatVO getTodayStats(Long userId);
}
