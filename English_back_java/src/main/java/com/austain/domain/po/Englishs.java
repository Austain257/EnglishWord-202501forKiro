package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Englishs {
    private Long id;
    private int userId;  // 用户ID，默认为管理员0
    private int bookId;  // 词库ID
    private String word;  // 单词
    private String chinese; // 中文
    private String pronounce; // 音标
    private String times; // 出现过的次数
    private Integer isGrasp; // 是否掌握0未掌握，1已掌握，2错词
    private Integer errorTimes; // 错误次数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
