package com.austain.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordRequest {
    private int id;  // 添加单词的id
    private int userId;
    private Long bookId;
    private Integer startId;
    private Integer endId;
    private String recordIds;
    private String learningRecord;
    private int type;
    private int selected;
    private int alreadyReviewed;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
