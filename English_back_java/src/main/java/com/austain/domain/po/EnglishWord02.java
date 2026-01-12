package com.austain.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnglishWord02 {
    private Long id;
    private Long userId;
    private Long bookId;
    private String word;
    private String chinese;
    private String pronounce;
    private Integer times;
    private Integer isGrasp;
    private Integer errorTimes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
