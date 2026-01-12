package com.austain.service.impl;

import com.austain.domain.dto.book.BookCreateRequest;
import com.austain.domain.dto.book.BookImportResult;
import com.austain.domain.dto.word.WordParseItem;
import com.austain.domain.po.EnglishWord02;
import com.austain.domain.po.UserBook;
import com.austain.mapper.UserBookMapper;
import com.austain.service.BookImportService;
import com.austain.util.PdfWordTableParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookImportServiceImpl implements BookImportService {

    private static final String DEFAULT_DESCRIPTION = "有点懒哦，还没给我添加介绍";
    private static final String DEFAULT_COVER = "https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/word_cover.jpg";
    private static final String DEFAULT_VISIBILITY = "PRIVATE";

    @Autowired
    private UserBookMapper userBookMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookImportResult createBookAndImportWords(BookCreateRequest request, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请上传有效的PDF文件");
        }
        List<WordParseItem> parsedItems = PdfWordTableParser.parse(file);
        if (parsedItems.isEmpty()) {
            throw new IllegalArgumentException("未能从文件中解析到有效单词");
        }

        UserBook userBook = buildUserBook(request, parsedItems.size());
        int inserted = userBookMapper.insert(userBook);
        if (inserted != 1 || userBook.getId() == null) {
            throw new IllegalStateException("课本创建失败，请稍后重试");
        }

        List<EnglishWord02> wordEntities = buildWordEntities(request.getUserId(), userBook.getId(), parsedItems);
        int imported = 0;
        if (!wordEntities.isEmpty()) {
            imported = userBookMapper.insertBatch(wordEntities);
        }
        return new BookImportResult(userBook.getId(), parsedItems.size(), imported);
    }

    private UserBook buildUserBook(BookCreateRequest request, int parsedCount) {
        UserBook userBook = new UserBook();
        userBook.setUserId(request.getUserId());
        userBook.setBookName(request.getBookName().trim());
        userBook.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : DEFAULT_DESCRIPTION);
        userBook.setCoverUrl(StringUtils.hasText(request.getCoverUrl()) ? request.getCoverUrl().trim() : DEFAULT_COVER);
        Integer wordCount = request.getWordCount() != null && request.getWordCount() > 0 ? request.getWordCount() : parsedCount;
        userBook.setWordCount(wordCount);
        String visibility = StringUtils.hasText(request.getVisibility()) ? request.getVisibility().trim().toUpperCase() : DEFAULT_VISIBILITY;
        userBook.setVisibility("PUBLIC".equals(visibility) ? "PUBLIC" : DEFAULT_VISIBILITY);
        userBook.setStatus(1);
        return userBook;
    }

    private List<EnglishWord02> buildWordEntities(Long userId, Long bookId, List<WordParseItem> items) {
        List<EnglishWord02> list = new ArrayList<>(items.size());
        for (WordParseItem item : items) {
            if (item == null || !StringUtils.hasText(item.getWord()) || !StringUtils.hasText(item.getMeaning())) {
                continue;
            }
            EnglishWord02 word = new EnglishWord02();
            word.setUserId(userId);
            word.setBookId(bookId);
            word.setWord(item.getWord().trim());
            word.setChinese(item.getMeaning().trim());
            word.setPronounce(StringUtils.hasText(item.getPronunciation()) ? item.getPronunciation().trim() : "");
            word.setTimes(parseFrequency(item.getFrequency()));
            word.setIsGrasp(0);
            word.setErrorTimes(0);
            list.add(word);
        }
        return list;
    }

    private int parseFrequency(String frequency) {
        if (!StringUtils.hasText(frequency)) {
            return 0;
        }
        try {
            return Integer.parseInt(frequency.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
