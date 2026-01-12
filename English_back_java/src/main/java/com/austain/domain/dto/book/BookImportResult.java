package com.austain.domain.dto.book;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookImportResult {
    private Long bookId;
    private int parsedCount;
    private int importedCount;
}
