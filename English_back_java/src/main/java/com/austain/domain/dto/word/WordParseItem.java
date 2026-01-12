package com.austain.domain.dto.word;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PDF解析后统一的单词条目结构.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordParseItem {
    /**
     * 单词原文.
     */
    private String word;

    /**
     * 音标（去掉/[]包裹符号）.
     */
    private String pronunciation;

    /**
     * 中文释义，仅取分号前第一段.
     */
    private String meaning;

    /**
     * 频次；若文件无该列则为空字符串.
     */
    private String frequency;
}
