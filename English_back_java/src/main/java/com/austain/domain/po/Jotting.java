package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jotting {
    private int id;
    private int userId; // 用户ID，默认为1
    private String word;  // 单词
    private String sentence;  // 句子
    private String chinese;  // 中文
    private int type;  // 类型，1为单词，0为句子
    private int reviewed;  // 是否已复习
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

}
