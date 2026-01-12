package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.dto.book.BookCreateRequest;
import com.austain.domain.dto.book.BookImportResult;
import com.austain.service.BookImportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件解析相关接口。
 */
@RestController
@RequestMapping("/api/files")
public class FileParseController {

    @Autowired
    private BookImportService bookImportService;

    /**
     * 上传PDF创建课本并导入单词
     */
    @PostMapping(value = "/parseWords", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result createBookAndImportWords(@Valid @RequestPart("book") BookCreateRequest request,
                                           @RequestPart("file") MultipartFile file) {
        try {
            BookImportResult result = bookImportService.createBookAndImportWords(request, file);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("创建课本失败：" + e.getMessage());
        }
    }
}
