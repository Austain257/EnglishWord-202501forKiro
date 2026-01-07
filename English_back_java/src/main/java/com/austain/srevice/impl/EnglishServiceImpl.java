package com.austain.srevice.impl;

import com.austain.domain.po.*;
import com.austain.mapper.EnglishMapper;
import com.austain.srevice.EnglishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EnglishServiceImpl implements EnglishService {

    @Autowired
    private EnglishMapper englishMapper;

    @Override
    public List<WordBook> getWordBookList(int userId) {
        return englishMapper.getWordBookList(userId);
    }

    @Override
    public boolean checkBookExists(int bookId) {
        return englishMapper.checkBookExists(bookId) > 0;
    }

    @Override
    public List<Englishs> getEnglishListByBookId(WordRequest request) {
//        // 计算数据库分页参数
//        int start = Math.max(0, request.getStart());
//        int count = Math.max(1, request.getEnd() - request.getStart() + 1);

        List<Englishs> wordList = new ArrayList<>();
                // 直接从数据库获取分页数据
        List<Englishs> allWordList = englishMapper.getEnglishListByBookCode(request.getUserId(),request.getBookId());
        if (allWordList.size() <= request.getEnd()) {  // 小于前端请求的结尾，为后面加1做兼容
            wordList = allWordList.subList(request.getStart(),allWordList.size());
        }
        else
            // TODO 数据库查到数据为0
            wordList = allWordList.subList(request.getStart(),request.getEnd() + 1);

        System.out.println("从后端获取到的单词数量：" + allWordList.size());


        // 随机打乱顺序
        Collections.shuffle(wordList);
        return wordList;
    }

    @Override
    public List<Englishs> getErrorWordList(WordRequest request) {

        // TODO 直接返回错词列表，后续需要实现算法，推荐错词，更好的学习
        return englishMapper.getErrorWordList(request);
    }

    @Override
    public boolean isGrasp(int id) {
        int result = englishMapper.isGrasp(id);
        return result > 0;
    }

    @Override
    public List<Sentence> getSentenceList(WordRequest sentenceRequest) {
        
        // 直接从数据库获取分页数据
        List<Sentence> sentenceList = englishMapper.getSentenceList(sentenceRequest.getUserId());
        
        // 随机打乱顺序
        Collections.shuffle(sentenceList);
        return sentenceList;
    }

    @Override
    public boolean setNotGrasp(int id) {
        int result = englishMapper.setNotGrasp(id);
        return result > 0;
    }

    @Override
    public List<Sentence> getErrorSentence(int userId) {
        return englishMapper.getErrorSentence(userId);
    }

    @Override
    public boolean sentenceNotGrasp(int id) {
        int result = englishMapper.sentenceNotGrasp(id);
        return result > 0;
    }

    @Override
    public boolean sentenceIsGrasp(int id) {
        int result = englishMapper.sentenceIsGrasp(id);
        return result > 0;
    }
}
