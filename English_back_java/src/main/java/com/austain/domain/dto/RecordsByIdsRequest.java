package com.austain.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 根据ID批量查询学习记录的请求
 */
@Data
public class RecordsByIdsRequest {
    /**
     * 用户ID，可选，用于鉴权
     */
    private Long userId;

    /**
     * 学习记录ID集合
     */
    private List<Long> recordIds;
}
