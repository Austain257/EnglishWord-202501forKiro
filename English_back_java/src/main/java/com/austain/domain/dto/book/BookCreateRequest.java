package com.austain.domain.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建课本并导入单词的请求参数.
 */
@Data
public class BookCreateRequest {

    @NotNull(message = "userId不能为空")
    private Long userId;

    @NotBlank(message = "课本名称不能为空")
    private String bookName;

    private String description;

    private String coverUrl;

    private Integer wordCount;

    /**
     * PRIVATE / PUBLIC.
     */
    private String visibility;

}
