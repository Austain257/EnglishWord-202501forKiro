package com.austain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotivationQuoteResult {

    private int id;  // 激励文案id
    private String content; // 激励文案内容
    private String author;  // 激励文案作者
    private int priority;  // 优先级
    private int status;  // 状态
    private LocalDateTime createTime;
}
