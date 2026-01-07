package com.austain.srevice;

import com.austain.srevice.StudySessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 学习会话定时任务
 */
@Component
public class StudySessionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(StudySessionScheduler.class);

    @Autowired
    private StudySessionService studySessionService;

    /**
     * 每5分钟执行一次：自动关闭超时会话
     * 超时时间设为3分钟，即3分钟无心跳则强制结束会话
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟 = 5 * 60 * 1000毫秒
    public void autoCloseTimeoutSessions() {
        try {
            int closedCount = studySessionService.autoCloseTimeoutSessions(3); // 3分钟超时
            if (closedCount > 0) {
                logger.info("自动关闭了 {} 个超时学习会话", closedCount);
            }
        } catch (Exception e) {
            logger.error("自动关闭超时会话失败", e);
        }
    }

    /**
     * 每天凌晨0:05执行：可用于统计数据汇总等维护任务
     * 这里暂时只记录日志，如需要其他维护任务可以在此添加
     */
    @Scheduled(cron = "0 5 0 * * ?") // 每天0:05
    public void dailyMaintenance() {
        try {
            logger.info("执行每日维护任务 - 学习会话数据维护");
            // 这里可以添加其他维护任务，如数据清理、统计报告等
        } catch (Exception e) {
            logger.error("每日维护任务执行失败", e);
        }
    }
}
