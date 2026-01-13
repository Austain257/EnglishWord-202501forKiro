package com.austain.service.impl;

import com.austain.domain.dto.*;
import com.austain.domain.po.WordStudyRecord;
import com.austain.mapper.WordStudyRecordMapper;
import com.austain.service.WordStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 单词学习服务实现类
 * @author kiro
 */
@Service
public class WordStudyServiceImpl implements WordStudyService {
    
    @Autowired
    private WordStudyRecordMapper wordStudyRecordMapper;
    
    @Override
    @Transactional
    public WordStudyRecord startStudySession(StudySessionRequest request) {
        // 检查是否有进行中的会话
        WordStudyRecord activeSession = wordStudyRecordMapper.selectActiveSession(request.getUserId());
        if (activeSession != null) {
            // 如果有进行中的会话，直接返回该会话信息，让前端继续使用
            return activeSession;
        }
        
        // 创建新的学习记录
        WordStudyRecord record = new WordStudyRecord();
        record.setUserId(request.getUserId());
        record.setBookId(request.getBookId());
        record.setStartTime(LocalDateTime.now());
        record.setStartId(request.getStartId());
        record.setEndTime(record.getStartTime());
        record.setEndId(request.getStartId());
        record.setStatus(1); // 1-进行中
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        
        int result = wordStudyRecordMapper.insertStudyRecord(record);
        if (result <= 0) {
            throw new RuntimeException("创建学习会话失败");
        }
        
        return record;
    }
    
    @Override
    @Transactional
    public WordStudyRecord endStudySession(Long sessionId, StudyEndRequest request) {
        WordStudyRecord record = wordStudyRecordMapper.selectById(sessionId);
        if (record == null) {
            throw new RuntimeException("学习会话不存在");
        }
        
        if (record.getStatus() != 1) {
            throw new RuntimeException("该学习会话已结束");
        }
        
        // 更新学习记录
        record.setEndTime(request.getActualEndTime());
        record.setEndId(request.getEndId());
        record.setStatus(0); // 0-正常结束
        record.setUpdateTime(LocalDateTime.now());
        
        int result = wordStudyRecordMapper.updateStudyRecord(record);
        if (result <= 0) {
            throw new RuntimeException("结束学习会话失败");
        }
        
        return record;
    }
    
    @Override
    public StudyLockStatusResponse checkReviewStatus(Long userId) {
        List<WordStudyRecord> unfinishedReviews = wordStudyRecordMapper.selectUnfinishedReviews(userId);
        
        StudyLockStatusResponse response = new StudyLockStatusResponse();
        
        if (unfinishedReviews.isEmpty()) {
            // 没有待复习任务，解锁状态
            response.setIsLocked(false);
            response.setReason("NO_PENDING_REVIEW");
            response.setAllowedActions(Arrays.asList("NEW_STUDY", "GAMES", "SETTINGS", "REVIEW", "JOTTINGS"));
            response.setBlockedActions(new ArrayList<>());
            response.setMessage("所有复习任务已完成，可以开始新的学习！");
            response.setPendingReviews(new ArrayList<>());
        } else {
            // 有待复习任务，锁定状态
            response.setIsLocked(true);
            response.setReason("PENDING_REVIEW");
            response.setAllowedActions(Arrays.asList("REVIEW", "JOTTINGS"));
            response.setBlockedActions(Arrays.asList("NEW_STUDY", "GAMES", "SETTINGS"));
            response.setMessage("您有未完成的复习任务，请先完成第一、二轮复习！");
            
            // 构建待复习列表
            List<StudyLockStatusResponse.PendingReview> pendingList = new ArrayList<>();
            for (WordStudyRecord record : unfinishedReviews) {
                StudyLockStatusResponse.PendingReview pending = new StudyLockStatusResponse.PendingReview();
                pending.setSessionId(record.getId());
                pending.setRange(record.getStartId() + "-" + record.getEndId());
                
                // 判断待完成的轮次
                if (record.getRound1ReviewTime() == null) {
                    pending.setPendingRound(1);
                } else if (record.getRound2ReviewTime() == null) {
                    pending.setPendingRound(2);
                } else {
                    pending.setPendingRound(3); // 理论上第1、2轮完成后就解锁了
                }
                
                pending.setCreateTime(record.getCreateTime());
                pendingList.add(pending);
            }
            response.setPendingReviews(pendingList);
        }
        
        return response;
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

        List<WordStudyRecord> records = wordStudyRecordMapper.selectByIds(sessionIds);
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

        // 校验前置轮次与状态
        for (WordStudyRecord record : records) {
            if (record.getStatus() == null || record.getStatus() != 0) {
                throw new RuntimeException("仅已完成的学习记录可以标记复习");
            }
            if (reviewRound > 1) {
                LocalDateTime previousRoundTime = getRoundTime(record, reviewRound - 1);
                if (previousRoundTime == null) {
                    throw new RuntimeException("请先完成第" + (reviewRound - 1) + "轮复习");
                }
            }
        }

        LocalDateTime completedTime = request.getCompletedTime() != null
            ? request.getCompletedTime()
            : LocalDateTime.now();

        for (Long id : sessionIds) {
            wordStudyRecordMapper.updateReviewTime(id, reviewRound, completedTime);
        }
        return true;
    }

    private LocalDateTime getRoundTime(WordStudyRecord record, int round) {
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
    
    @Override
    public List<WordStudyRecord> getTodayRecords(Long userId) {
        return wordStudyRecordMapper.selectTodayRecords(userId);
    }
    
    @Override
    public boolean hasUnfinishedReview(Long userId) {
        List<WordStudyRecord> unfinishedReviews = wordStudyRecordMapper.selectUnfinishedReviews(userId);
        return !unfinishedReviews.isEmpty();
    }

    @Override
    public WordStudyRecord getLatestFinishedRecord(Long userId) {
        return wordStudyRecordMapper.selectLatestFinishedRecord(userId);
    }

    @Override
    public WordStudyRecord getLatestFinishedRecord(Long userId, Long bookId) {
        if (bookId == null) {
            return getLatestFinishedRecord(userId);
        }
        return wordStudyRecordMapper.selectLatestFinishedRecordByBook(userId, bookId);
    }

    @Override
    public List<WordStudyRecord> getRecordsByIds(Long userId, List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<WordStudyRecord> records = wordStudyRecordMapper.selectByIds(recordIds);
        if (records == null || records.isEmpty()) {
            return records;
        }

        if (userId != null) {
            boolean invalidOwner = records.stream().anyMatch(record -> !userId.equals(record.getUserId()));
            if (invalidOwner) {
                throw new RuntimeException("存在无权访问的学习记录");
            }
        }

        return records;
    }

    /**
     * 解析请求中的 sessionIds 或 recordIdsText 或单个 sessionId
     */
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
                    if (id > 0) {
                        ids.add(id);
                    }
                } catch (NumberFormatException ignore) {
                }
            }
        }

        if (ids.isEmpty() && request.getSessionId() != null && request.getSessionId() > 0) {
            ids.add(request.getSessionId());
        }

        return new ArrayList<>(ids);
    }
}
