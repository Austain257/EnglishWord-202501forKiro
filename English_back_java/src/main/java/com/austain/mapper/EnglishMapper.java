package com.austain.mapper;

import com.austain.domain.po.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EnglishMapper {

    // 获取词库列表（包括用户自己的课本和公开的课本）
    List<WordBook> getWordBookList(int userId);

    // 根据词库代码获取单词列表  TODO 为这三个键加索引
    List<Englishs> getEnglishListByBookCode(int userId, int bookId);

    // 获取错词列表（is_grasp=2的单词）
    @Select("select * from english_word_01 where user_id = #{userId} and book_id = #{bookId} and is_grasp = 2")
    List<Englishs> getErrorWordList(WordRequest request);

    // 获取单词总数
//    @Select("SELECT COUNT(*) FROM english_word_01 WHERE user_id IN (#{userId},0,1,2,3,4,5)")
    @Select("SELECT COUNT(*) FROM english_word_01 WHERE user_id = #{userId}")
    int countTotalWords(@Param("userId") int userId);

    // 获取已掌握单词总数
//    @Select("SELECT COUNT(*) FROM english_word_01 WHERE user_id IN (#{userId},0,1,2,3,4,5) AND is_grasp = 1")
    @Select("SELECT COUNT(*) FROM english_word_02 WHERE user_id = #{userId} AND is_grasp = 1")
    int countMasteredWords(@Param("userId") int userId);

    // 获取未掌握单词总数
//    @Select("SELECT COUNT(*) FROM english_word_01 WHERE user_id IN (#{userId},0,1,2,3,4,5) AND is_grasp = 2")
    @Select("SELECT COUNT(*) FROM english_word_02 WHERE user_id = #{userId} AND is_grasp = 2")
    int countErrorWords(@Param("userId") int userId);

    // 获取今日已掌握单词总数
    int countTodayMasteredWords(@Param("userId") int userId, @Param("bookId") Integer bookId);

    // 获取今日错词总数
    int countTodayErrorWords(@Param("userId") int userId, @Param("bookId") Integer bookId);

    // 标记已掌握，在原数据表中修改字段
    @Update("UPDATE english_word_02 SET is_grasp = 1, update_time = NOW() where id = #{id}")
    int isGrasp(int id);

    // 获取句子列表
    @Select("select * from english_sentence01 where user_id = #{userId} and is_grasp != 1")
    List<Sentence> getSentenceList(@Param("userId") int userId);

    // 检查词库是否存在
    @Select("SELECT COUNT(*) FROM user_books WHERE id = #{bookId} AND status = 1")
    int checkBookExists(int bookId);

    // 获取用户指定课本的所有单词（用于统计）
//    @Select("select * from english_word_01 where user_id in (#{userId},0,1,2,3,4,5) and book_id = #{bookId}")
    @Select("select * from english_word_02 where user_id = #{userId} and book_id = #{bookId}")
    List<Englishs> getAllWordsByUserAndBook(@Param("userId") int userId, @Param("bookId") int bookId);

    // 获取用户的所有句子（用于统计）
    @Select("select * from english_sentence01 where user_id in (#{userId},0,1,2,3,4,5)")
    List<Sentence> getAllSentencesByUser(@Param("userId") int userId);

    @Select("SELECT COUNT(*) FROM english_sentence01 WHERE user_id = #{userId}")
    int countTotalSentences(@Param("userId") int userId);

    @Select("SELECT COUNT(*) FROM english_sentence01 WHERE user_id = #{userId} AND is_grasp = 1")
    int countMasteredSentences(@Param("userId") int userId);

    @Select("SELECT COUNT(*) FROM english_sentence01 WHERE user_id = #{userId} AND is_grasp = 2")
    int countErrorSentences(@Param("userId") int userId);

    // 在原数据表中标记未掌握单词
    @Update("UPDATE english_word_02 SET is_grasp = 2, error_times = error_times + 1, update_time = NOW() where id = #{id}")
    int setNotGrasp(int id);

    // 获取用户所有未掌握的句子
    @Select("select * from english_sentence01 where user_id = #{userId} and is_grasp = 2")
    List<Sentence> getErrorSentence(int userId);

    @Update("UPDATE english_sentence01 SET is_grasp = 2, error_times = error_times + 1 where id = #{id}")
    int sentenceNotGrasp(int id);

    @Update("UPDATE english_sentence01 SET is_grasp = 1 where id = #{id}")
    int sentenceIsGrasp(int id);
}
