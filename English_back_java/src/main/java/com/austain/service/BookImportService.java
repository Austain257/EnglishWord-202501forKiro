package com.austain.service;

import com.austain.domain.dto.book.BookCreateRequest;
import com.austain.domain.dto.book.BookImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface BookImportService {
    BookImportResult createBookAndImportWords(BookCreateRequest request, MultipartFile file) throws Exception;
}
