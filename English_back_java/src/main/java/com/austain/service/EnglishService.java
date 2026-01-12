package com.austain.service;

import com.austain.domain.po.*;

import java.util.List;

public interface EnglishService {
    // 获取词库列表
    List<WordBook> getWordBookList(int userId);
    
    // 检查词库是否存在
    boolean checkBookExists(int bookCode);
    
    // 根据词库代码获取单词列表
    List<Englishs> getEnglishListByBookId(WordRequest request);

    // 获取错词列表
    List<Englishs> getErrorWordList(WordRequest request);

    // 从复习集合移除单词
    boolean isGrasp(int id);

    // 获取句子列表
    List<Sentence> getSentenceList(WordRequest sentenceRequest);

    // 添加错词 -- 在原数据标记
    boolean setNotGrasp(int id);

    List<Sentence> getErrorSentence(int userId);

    boolean sentenceNotGrasp(int id);

    boolean sentenceIsGrasp(int id);
}
