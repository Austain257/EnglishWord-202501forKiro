package com.austain.handler;

import com.austain.domain.dto.RecordResult;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 艾宾浩斯复习算法实现
 * 注意：此类已废弃，新的数据库结构使用study_records表按日期自动管理复习计划
 * @deprecated 使用新的user_word_progress表来管理单词复习进度
 */
@Deprecated
public class SimpleEbbinghausReview {
    
    // 艾宾浩斯复习间隔（天数）
    private static final int[] REVIEW_INTERVALS = {0, 1, 2, 4, 7, 15, 30};
    
    /**
     * 获取需要复习的内容列表
     * @deprecated 此方法已废弃，请使用新的复习系统
     */
    @Deprecated
    public List<RecordResult> getReviewItems(List<RecordResult> allRecords, LocalDate today) {
        List<RecordResult> reviewList = new ArrayList<>();
        
        for (RecordResult record : allRecords) {
            if (record.getCreateTime() == null) {
                continue;
            }
            
            LocalDate createDate = record.getCreateTime().toLocalDate(); // 获取创建日期
            long daysBetween = ChronoUnit.DAYS.between(createDate, today);  // 计算两个日期之间的天数差
            
            // 检查是否在复习时间点
            for (int interval : REVIEW_INTERVALS) {
                if (Math.abs(daysBetween - interval) == 0) { // 如果在复习时间点，添加进去返回的列表
                    // 注意：新的RecordPO结构不再有setToDate方法
                    // 可以通过extraData字段存储额外信息
                    reviewList.add(record); // 添加到需要复习的列表中
                    break;
                }
            }
        }
        
        return reviewList;
    }
}
