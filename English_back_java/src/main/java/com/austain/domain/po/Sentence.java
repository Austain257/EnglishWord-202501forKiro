package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sentence {
    private int id;
    private int userId;
    private String sentence; // 对应数据库的english_sentence字段
    private String chinese; // 对应数据库的chinese_translation字段
    private int isGrasp;  // 0-未掌握，1-已掌握, 2-错句
    private int errorTimes;  // 错误次数
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
