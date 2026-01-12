package com.austain.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBook {
    private Long id;
    private Long userId;
    private String bookName;
    private String description;
    private String coverUrl;
    private Integer wordCount;
    private String visibility;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
