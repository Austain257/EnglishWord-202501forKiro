package com.austain.util;

import com.austain.domain.dto.word.WordParseItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类：解析单词类PDF表格，兼容多种列顺序，统一返回 {@link WordParseItem}.
 */
public final class PdfWordTableParser {

    private static final Pattern LINE_PATTERN = Pattern.compile("^\\s*(\\d+)[\\.．、:：]?\\s*(.*)$");
    private static final Pattern PHONETIC_PATTERN = Pattern.compile("(/[^/]+/|\\[[^\\]]+\\])");
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final Pattern INLINE_FREQUENCY_PATTERN = Pattern.compile("(?:\\(|（)?\\s*(?:考频|考頻)\\s*(\\d{1,5})\\s*次\\s*(?:\\)|）)?");
    private static final Pattern TRAILING_FREQUENCY_PATTERN = Pattern.compile("(.+?)\\s+(\\d{1,5})$");
    private static final Pattern ENTRY_SPLIT_PATTERN = Pattern.compile("(?=\\b\\d+(?:[\\.．、:：]|\\s+(?=[A-Za-z])))");
    private static final Pattern NUMBER_ONLY_PATTERN = Pattern.compile("^\\s*(\\d+)[\\.．、:：]?$");

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
        Map<String, WordParseItem> deduplicated = new LinkedHashMap<>();

        for (WordParseItem item : parseTableRows(lines)) {
            deduplicated.putIfAbsent(buildDedupKey(item), item);
        }

        for (WordParseItem item : parseNumberedParagraphs(lines)) {
            deduplicated.putIfAbsent(buildDedupKey(item), item);
        }

        for (String rawLine : lines) {
            for (String segment : splitIntoEntries(rawLine)) {
                parseSingleEntry(segment).ifPresent((item) ->
                        deduplicated.putIfAbsent(buildDedupKey(item), item)
                );
            }
        }
        return new ArrayList<>(deduplicated.values());
    }

    private static List<WordParseItem> parseTableRows(String[] lines) {
        List<WordParseItem> results = new ArrayList<>();
        TableRow currentRow = null;
        boolean tableMode = false;
        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine.replace('\u00A0', ' ').trim();
            if (line.isEmpty()) {
                continue;
            }
            if (isHeaderLine(line)) {
                flushRow(results, currentRow);
                currentRow = null;
                tableMode = true;
                continue;
            }
            if (!tableMode) {
                continue;
            }
            if (isRowNumber(line)) {
                flushRow(results, currentRow);
                currentRow = new TableRow();
                continue;
            }
            if (currentRow == null) {
                continue;
            }
            if (currentRow.word == null && isLikelyWord(line)) {
                currentRow.word = normalizeWord(line);
                continue;
            }
            if (currentRow.pronunciation == null && isLikelyPhonetic(line)) {
                currentRow.pronunciation = line;
                continue;
            }
            currentRow.appendMeaning(line);
        }
        flushRow(results, currentRow);
        return results;
    }

    private static void flushNumberedEntry(List<WordParseItem> results, NumberedEntry entry) {
        if (results == null || entry == null) {
            return;
        }
        WordParseItem item = entry.toItem();
        if (item != null) {
            results.add(item);
        }
    }

    private static String sanitizeLine(String line) {
        if (line == null) {
            return "";
        }
        return line.replace('\u00A0', ' ')
                .replace('\t', ' ')
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private static boolean hasAlphaOrSlash(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        if (PHONETIC_PATTERN.matcher(text).find()) {
            return true;
        }
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    private static List<WordParseItem> parseNumberedParagraphs(String[] lines) {
        List<WordParseItem> results = new ArrayList<>();
        NumberedEntry currentEntry = null;
        for (String rawLine : lines) {
            String line = sanitizeLine(rawLine);
            if (line.isEmpty()) {
                continue;
            }
            Matcher numberedLineMatcher = LINE_PATTERN.matcher(line);
            if (numberedLineMatcher.matches() && hasAlphaOrSlash(numberedLineMatcher.group(2))) {
                flushNumberedEntry(results, currentEntry);
                currentEntry = new NumberedEntry(numberedLineMatcher.group(1));
                currentEntry.append(numberedLineMatcher.group(2));
                continue;
            }
            if (NUMBER_ONLY_PATTERN.matcher(line).matches()) {
                flushNumberedEntry(results, currentEntry);
                currentEntry = new NumberedEntry(line.replaceAll("\\D+", ""));
                continue;
            }
            if (currentEntry != null) {
                currentEntry.append(line);
            }
        }
        flushNumberedEntry(results, currentEntry);
        return results;
    }

    private static List<String> splitIntoEntries(String rawLine) {
        if (rawLine == null) {
            return Collections.emptyList();
        }
        String normalized = rawLine.replace('\u00A0', ' ').trim();
        if (normalized.isEmpty()) {
            return Collections.emptyList();
        }
        Matcher matcher = ENTRY_SPLIT_PATTERN.matcher(normalized);
        List<Integer> starts = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.start());
        }
        if (starts.size() <= 1) {
            return Collections.singletonList(normalized);
        }
        List<String> segments = new ArrayList<>();
        for (int i = 0; i < starts.size(); i++) {
            int start = starts.get(i);
            int end = i + 1 < starts.size() ? starts.get(i + 1) : normalized.length();
            if (start >= normalized.length()) {
                continue;
            }
            String segment = normalized.substring(start, Math.min(end, normalized.length())).trim();
            if (!segment.isEmpty() && Character.isDigit(segment.charAt(0))) {
                segments.add(segment);
            }
        }
        return segments.isEmpty() ? Collections.singletonList(normalized) : segments;
    }

    private static Optional<WordParseItem> parseSingleEntry(String rawLine) {
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
            String candidate = phoneticMatcher.group();
            if (containsChinese(candidate)) {
                continue;
            }
            phoneticText = candidate;
            phoneticStart = phoneticMatcher.start();
            phoneticEnd = phoneticMatcher.end();
            break;
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
        Matcher inlineMatcher = INLINE_FREQUENCY_PATTERN.matcher(meaningText);
        if (inlineMatcher.find()) {
            frequency = inlineMatcher.group(1);
            meaningText = (meaningText.substring(0, inlineMatcher.start()) + " " + meaningText.substring(inlineMatcher.end())).trim();
        }

        if (frequency == null || frequency.isEmpty()) {
            Matcher trailingMatcher = TRAILING_FREQUENCY_PATTERN.matcher(meaningText);
            if (trailingMatcher.matches()) {
                frequency = trailingMatcher.group(2);
                meaningText = trailingMatcher.group(1).trim();
            }
        }

        meaningText = meaningText.replace("；", ";").replaceAll("\\s{2,}", " ").trim();
        int semicolonIndex = meaningText.indexOf(';');
        if (semicolonIndex >= 0) {
            meaningText = meaningText.substring(0, semicolonIndex).trim();
        }
        return new ParsedMeaning(meaningText, frequency == null ? "" : frequency.trim());
    }

    private static boolean containsChinese(String content) {
        return content != null && CHINESE_PATTERN.matcher(content).find();
    }

    private static class NumberedEntry {
        private final String number;
        private final StringBuilder content = new StringBuilder();

        private NumberedEntry(String number) {
            this.number = number == null ? "" : number.trim();
        }

        private void append(String line) {
            if (line == null || line.isBlank()) {
                return;
            }
            if (content.length() > 0) {
                content.append(' ');
            }
            content.append(line.trim());
        }

        private WordParseItem toItem() {
            if (number.isEmpty() || content.length() == 0) {
                return null;
            }
            return parseSingleEntry(number + " " + content).orElse(null);
        }
    }

    private static void flushRow(List<WordParseItem> results, TableRow row) {
        if (row == null) {
            return;
        }
        WordParseItem item = row.toItem();
        if (item != null) {
            results.add(item);
        }
    }

    private static String buildDedupKey(WordParseItem item) {
        if (item == null) {
            return "";
        }
        String word = Optional.ofNullable(item.getWord()).orElse("").trim().toLowerCase();
        String meaning = Optional.ofNullable(item.getMeaning()).orElse("").trim().toLowerCase();
        return word + "|" + meaning;
    }

    private static boolean isHeaderLine(String line) {
        if (line == null) {
            return false;
        }
        String normalized = line.replaceAll("\\s+", "");
        return normalized.contains("序号") && (normalized.contains("单词") || normalized.contains("词汇"));
    }

    private static boolean isRowNumber(String line) {
        return line != null && line.matches("^\\d{1,4}[\\.．、]?$");
    }

    private static boolean isLikelyWord(String line) {
        return line != null && line.matches("^[A-Za-z][A-Za-z\\-' ]{0,30}$");
    }

    private static boolean isLikelyPhonetic(String line) {
        return line != null && PHONETIC_PATTERN.matcher(line.trim()).matches();
    }

    private static class TableRow {
        private String word;
        private String pronunciation;
        private final StringBuilder meaningBuilder = new StringBuilder();

        private void appendMeaning(String line) {
            if (line == null || line.isBlank()) {
                return;
            }
            if (meaningBuilder.length() > 0) {
                meaningBuilder.append(' ');
            }
            meaningBuilder.append(line.trim());
        }

        private WordParseItem toItem() {
            if (word == null || word.isBlank() || pronunciation == null || pronunciation.isBlank() || meaningBuilder.length() == 0) {
                return null;
            }
            ParsedMeaning parsedMeaning = extractMeaningAndFrequency(meaningBuilder.toString());
            if (parsedMeaning.meaning().isEmpty()) {
                return null;
            }
            return new WordParseItem(
                    normalizeWord(word),
                    normalizePhonetic(pronunciation),
                    parsedMeaning.meaning(),
                    parsedMeaning.frequency()
            );
        }
    }

    private record ParsedMeaning(String meaning, String frequency) {
    }
}
