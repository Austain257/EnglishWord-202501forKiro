package com.austain.srevice.impl;

import com.austain.mapper.StudyRecordMapper;
import com.austain.srevice.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    // 每天 00:00 执行定时任务（注意：使用 24 小时制，0 0 0 * * ? 表示 秒 分 时 日 月 周）
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Shanghai")
    @Override
    public void resetScoreAtMidnight() {
        System.out.println("执行每日定时任务...");
        // 新的数据库结构使用study_records表，按日期自动分组，不需要重置操作
        // 如果需要清理旧数据，可以在这里添加清理逻辑
        studyRecordMapper.resetSelect();
        System.out.println("定时任务执行完成！");
    }
}

