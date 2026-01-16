package com.austain.service.impl;

import com.austain.domain.dto.ReviewCompleteRequest;
import com.austain.domain.dto.SentenceStudyRecordRequest;
import com.austain.domain.po.SentenceStudyRecord;
import com.austain.mapper.SentenceStudyRecordMapper;
import com.austain.service.SentenceStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SentenceStudyServiceImpl implements SentenceStudyService {

    @Autowired
    private SentenceStudyRecordMapper sentenceStudyRecordMapper;

    @Override
    public int saveRecord(SentenceStudyRecordRequest request) {
        if (request == null || request.getUserId() == null) {
            return 0;
        }
        SentenceStudyRecord record = new SentenceStudyRecord();
        record.setUserId(request.getUserId());
        record.setBookId(request.getBookId() != null ? request.getBookId() : 9999L);
        record.setStartId(request.getStartId());
        record.setEndId(request.getEndId());
        record.setStatus(request.getStatus() != null ? request.getStatus() : 0);

        LocalDateTime now = LocalDateTime.now();
        record.setStartTime(request.getStartTime() != null ? request.getStartTime() : now);
        record.setEndTime(request.getEndTime() != null ? request.getEndTime() : now);

        return sentenceStudyRecordMapper.insertRecord(record);
    }

    @Override
    public SentenceStudyRecord getLatestFinishedRecord(Long userId, Long bookId) {
        if (bookId == null) {
            return sentenceStudyRecordMapper.selectLatestFinishedRecord(userId);
        }
        return sentenceStudyRecordMapper.selectLatestFinishedRecordByBook(userId, bookId);
    }

    @Override
    public List<SentenceStudyRecord> getRecordsByIds(Long userId, List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<SentenceStudyRecord> records = sentenceStudyRecordMapper.selectByIds(recordIds);
        if (records == null || records.isEmpty()) {
            return records;
        }
        if (userId != null) {
            boolean invalidOwner = records.stream().anyMatch(r -> !userId.equals(r.getUserId()));
            if (invalidOwner) {
                throw new RuntimeException("存在无权访问的学习记录");
            }
        }
        return records;
    }

    @Override
    @Transactional
    public boolean markReviewComplete(ReviewCompleteRequest request) {
        if (request == null || request.getReviewRound() == null) {
            throw new IllegalArgumentException("复习标记参数不完整");
        }

        List<Long> sessionIds = resolveSessionIds(request);
        if (sessionIds.isEmpty()) {
            throw new IllegalArgumentException("未提供有效的学习记录ID");
        }

        int reviewRound = request.getReviewRound();
        if (reviewRound < 1 || reviewRound > 9) {
            throw new IllegalArgumentException("复习轮次超出范围");
        }

        List<SentenceStudyRecord> records = sentenceStudyRecordMapper.selectByIds(sessionIds);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("未找到对应的学习记录");
        }
        if (records.size() != sessionIds.size()) {
            throw new RuntimeException("部分学习记录不存在或已被删除");
        }

        Long userId = request.getUserId();
        if (userId != null) {
            boolean invalidOwner = records.stream().anyMatch(r -> !userId.equals(r.getUserId()));
            if (invalidOwner) {
                throw new RuntimeException("存在无权操作的学习记录");
            }
        }

        for (SentenceStudyRecord record : records) {
            if (record.getStatus() == null || record.getStatus() != 0) {
                throw new RuntimeException("仅已完成的学习记录可以标记复习");
            }
            if (reviewRound > 1) {
                LocalDateTime previous = getRoundTime(record, reviewRound - 1);
                if (previous == null) {
                    throw new RuntimeException("请先完成第" + (reviewRound - 1) + "轮复习");
                }
            }
            // 已标记过当前轮次，禁止重复
            if (getRoundTime(record, reviewRound) != null) {
                throw new RuntimeException("第" + reviewRound + "轮已完成，不能重复标记");
            }
        }

        LocalDateTime completedTime = LocalDateTime.now();
        for (Long id : sessionIds) {
            sentenceStudyRecordMapper.updateReviewTime(id, reviewRound, completedTime);
        }
        return true;
    }

    private List<Long> resolveSessionIds(ReviewCompleteRequest request) {
        Set<Long> ids = new HashSet<>();
        if (request.getSessionIds() != null) {
            request.getSessionIds().forEach(id -> {
                if (id != null && id > 0) {
                    ids.add(id);
                }
            });
        }
        if (ids.isEmpty() && request.getRecordIdsText() != null) {
            String text = request.getRecordIdsText();
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d+").matcher(text);
            while (matcher.find()) {
                try {
                    long id = Long.parseLong(matcher.group());
                    if (id > 0) ids.add(id);
                } catch (NumberFormatException ignore) {
                }
            }
        }
        if (ids.isEmpty() && request.getSessionId() != null && request.getSessionId() > 0) {
            ids.add(request.getSessionId());
        }
        return new ArrayList<>(ids);
    }

    private LocalDateTime getRoundTime(SentenceStudyRecord record, int round) {
        switch (round) {
            case 1:
                return record.getRound1ReviewTime();
            case 2:
                return record.getRound2ReviewTime();
            case 3:
                return record.getRound3ReviewTime();
            case 4:
                return record.getRound4ReviewTime();
            case 5:
                return record.getRound5ReviewTime();
            case 6:
                return record.getRound6ReviewTime();
            case 7:
                return record.getRound7ReviewTime();
            case 8:
                return record.getRound8ReviewTime();
            case 9:
                return record.getRound9ReviewTime();
            default:
                return null;
        }
    }
}
