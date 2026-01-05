package com.austain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordResult {
    private int id;  // 添加单词的id
    private int userId;
    private String learningRecord;
    private int type;
    private int selected;
    private int alreadyReviewed;
    private LocalDate dueDate;  // 截止时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
