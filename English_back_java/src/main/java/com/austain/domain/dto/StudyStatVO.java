package com.austain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学习统计数据返回VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyStatVO {
    
    /**
     * 今日累计学习秒数
     */
    private Integer dayTotalSec;
    
    /**
     * 累计学习秒数
     */
    private Long totalSec;
    
    /**
     * 会话ID（用于开始会话响应）
     */
    private Long sessionId;
    
    /**
     * 服务器时间
     */
    private LocalDateTime serverTime;
    
    /**
     * 今日学习时长格式化显示（XhYmin）
     */
    private String dayTotalDisplay;
    
    /**
     * 累计学习时长格式化显示（XhYmin）
     */
    private String totalDisplay;
    
    /**
     * 构造方法 - 用于开始会话响应
     */
    public StudyStatVO(Long sessionId, LocalDateTime serverTime) {
        this.sessionId = sessionId;
        this.serverTime = serverTime;
    }
    
    /**
     * 构造方法 - 用于统计数据响应
     */
    public StudyStatVO(Integer dayTotalSec, Long totalSec) {
        this.dayTotalSec = dayTotalSec;
        this.totalSec = totalSec;
        this.dayTotalDisplay = formatDuration(dayTotalSec != null ? dayTotalSec : 0);
        this.totalDisplay = formatDuration(totalSec != null ? totalSec.intValue() : 0);
    }
    
    /**
     * 格式化时长为 XhYmin 格式
     */
    private String formatDuration(int seconds) {
        if (seconds <= 0) {
            return "0h0min";
        }
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        return hours + "h" + minutes + "min";
    }
    
    /**
     * 设置统计数据并格式化显示
     */
    public void setStatData(Integer dayTotalSec, Long totalSec) {
        this.dayTotalSec = dayTotalSec;
        this.totalSec = totalSec;
        this.dayTotalDisplay = formatDuration(dayTotalSec != null ? dayTotalSec : 0);
        this.totalDisplay = formatDuration(totalSec != null ? totalSec.intValue() : 0);
    }
}
