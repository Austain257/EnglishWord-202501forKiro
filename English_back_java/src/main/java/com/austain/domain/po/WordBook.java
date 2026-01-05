package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordBook {
    private Integer id;
    private Integer userId;
    private String bookName; // 词库名称
    private String description; // 描述
    private Integer wordCount; // 单词数量
    private String visibility; // 可见性：1-公开，0-私有
    private Integer status; // 状态：1-启用，0-禁用
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}