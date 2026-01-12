package com.austain.util;

import com.austain.domain.dto.word.WordParseItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类：解析单词类PDF表格，兼容多种列顺序，统一返回 {@link WordParseItem}.
 */
public final class PdfWordTableParser {

    private static final Pattern LINE_PATTERN = Pattern.compile("^\\s*(\\d+)[\\.．、]?\\s+(.*)$");
    private static final Pattern PHONETIC_PATTERN = Pattern.compile("(/[^/]+/|\\[[^\\]]+\\])");
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final Pattern TRAILING_FREQUENCY_PATTERN = Pattern.compile("(.+?)\\s+(\\d{1,5})$");

    private PdfWordTableParser() {
    }

    /**
     * 解析上传的PDF文件（Spring MVC上传场景）.
     *
     * @param file 上传文件
     * @return 单词列表
     * @throws IOException 读取异常
     */
    public static List<WordParseItem> parse(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Collections.emptyList();
        }
        try (InputStream inputStream = file.getInputStream()) {
            return parse(inputStream);
        }
    }

    /**
     * 解析任意输入流，方便单元测试或离线任务.
     *
     * @param inputStream PDF输入流
     * @return 单词列表
     * @throws IOException 读取异常
     */
    public static List<WordParseItem> parse(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            return parse(text);
        }
    }

    /**
     * 核心解析逻辑，对抽取出的纯文本进行拆行、匹配.
     */
    public static List<WordParseItem> parse(String pdfText) {
        if (pdfText == null || pdfText.isBlank()) {
            return Collections.emptyList();
        }
        String[] lines = pdfText.split("\\r?\\n");
        List<WordParseItem> items = new ArrayList<>();
        for (String rawLine : lines) {
            parseLine(rawLine).ifPresent(items::add);
        }
        return items;
    }

    private static Optional<WordParseItem> parseLine(String rawLine) {
        if (rawLine == null) {
            return Optional.empty();
        }
        String normalized = rawLine
                .replace('\u00A0', ' ') // 去掉不间断空格
                .trim();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        Matcher lineMatcher = LINE_PATTERN.matcher(normalized);
        if (!lineMatcher.find()) {
            return Optional.empty();
        }
        String body = lineMatcher.group(2).trim();
        Matcher phoneticMatcher = PHONETIC_PATTERN.matcher(body);
        String phoneticText = null;
        int phoneticStart = -1;
        int phoneticEnd = -1;
        while (phoneticMatcher.find()) {
            phoneticText = phoneticMatcher.group();
            phoneticStart = phoneticMatcher.start();
            phoneticEnd = phoneticMatcher.end();
        }
        if (phoneticText == null || phoneticStart < 0) {
            return Optional.empty();
        }
        String beforePhonetic = body.substring(0, phoneticStart).trim();
        String afterPhonetic = body.substring(phoneticEnd).trim();
        if (beforePhonetic.isEmpty()) {
            return Optional.empty();
        }

        String word;
        String meaningCandidate;

        String[] wordAndTail = beforePhonetic.split("\\s+", 2);
        word = normalizeWord(wordAndTail[0]);
        String tail = wordAndTail.length > 1 ? wordAndTail[1].trim() : "";
        boolean meaningBeforePhonetic = containsChinese(tail);

        if (meaningBeforePhonetic) {
            meaningCandidate = concatMeaningSegments(tail, afterPhonetic);
        } else if (!afterPhonetic.isEmpty()) {
            meaningCandidate = afterPhonetic;
        } else {
            meaningCandidate = tail;
        }

        ParsedMeaning parsedMeaning = extractMeaningAndFrequency(meaningCandidate);
        if (word.isEmpty() || parsedMeaning.meaning().isEmpty()) {
            return Optional.empty();
        }

        WordParseItem item = new WordParseItem(
                word,
                normalizePhonetic(phoneticText),
                parsedMeaning.meaning(),
                parsedMeaning.frequency()
        );
        return Optional.of(item);
    }

    private static String concatMeaningSegments(String first, String second) {
        if (first.isBlank()) {
            return second;
        }
        if (second.isBlank()) {
            return first;
        }
        return (first + " " + second).trim();
    }

    private static String normalizeWord(String word) {
        return word == null ? "" : word.replaceAll("[^A-Za-z\\-']", "").trim();
    }

    private static String normalizePhonetic(String phoneticRaw) {
        if (phoneticRaw == null) {
            return "";
        }
        String trimmed = phoneticRaw.trim();
        if ((trimmed.startsWith("/") && trimmed.endsWith("/")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed.trim();
    }

    private static ParsedMeaning extractMeaningAndFrequency(String rawMeaning) {
        if (rawMeaning == null) {
            return new ParsedMeaning("", "");
        }
        String meaningText = rawMeaning.replace('\u3000', ' ').trim();
        if (meaningText.isEmpty()) {
            return new ParsedMeaning("", "");
        }

        String frequency = "";
        Matcher matcher = TRAILING_FREQUENCY_PATTERN.matcher(meaningText);
        if (matcher.matches()) {
            frequency = matcher.group(2);
            meaningText = matcher.group(1).trim();
        }

        meaningText = meaningText.replace("；", ";");
        int semicolonIndex = meaningText.indexOf(';');
        if (semicolonIndex >= 0) {
            meaningText = meaningText.substring(0, semicolonIndex).trim();
        }
        return new ParsedMeaning(meaningText, frequency);
    }

    private static boolean containsChinese(String content) {
        return content != null && CHINESE_PATTERN.matcher(content).find();
    }

    private record ParsedMeaning(String meaning, String frequency) {
    }
}
