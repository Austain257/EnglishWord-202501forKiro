package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.po.Englishs;
import com.austain.domain.po.Sentence;
import com.austain.domain.po.WordBook;
import com.austain.domain.po.WordRequest;
import com.austain.exception.InvalidWordRangeException;
import com.austain.mapper.EnglishMapper;
import com.austain.mapper.StudyStatMapper;
import com.austain.service.EnglishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/english")
public class EnglishController {

    // TODO 2026.01.04 09:02
//     1、一刷新页面选择的课本就丢失，显示未选择课本
//     2、错词本页面报错
//     3、积累本未成功获取
//     4、学习清单未成功获取
//     5、激励句修改获取逻辑
//     6、页面的统计数据需要确定是否从后端查询

    // TODO 2026.01.04 13:42
    // 1、错词本逻辑错误，并没有向后端你发起请求

    @Autowired
    private EnglishService englishService;
    
    @Autowired
    private EnglishMapper englishMapper;
    
    @Autowired
    private StudyStatMapper studyStatMapper;

    /**
     * 获取词库列表
     */
    @GetMapping("/books/{userId}")
    public Result getWordBookList(@PathVariable int userId) {
        System.out.println("获取词库列表已触发");
        List<WordBook> wordBooks = englishService.getWordBookList(userId);
        if (wordBooks == null) {
            return Result.success("你有点懒哦，还没有创建课本");
        }
        return Result.success(wordBooks);
    }

    /**
     * 检查词库是否存在
     */
    @GetMapping("/books/{bookId}/check")   // 不用校验用户id，因为bookId是唯一的
    public Result checkBook(@PathVariable int bookId) {
        System.out.println("检查词库是否存在已触发");
        boolean exists = englishService.checkBookExists(bookId);
        return Result.success(exists);
    }

    /**
     * 根据词课本id获取单词列表  -- 背诵页面，游戏页面、听写页面其他相关获取单词列表的页面  -- 已掌握单词不返回
     */
    @PostMapping("/wordList")
    public Result getWordsByBookCode(@RequestBody WordRequest request) {
        System.out.println("根据词课本id获取单词列表已触发");
        try {
            List<Englishs> englishs = englishService.getEnglishListByBookId(request);
            if (englishs == null || englishs.isEmpty()) {
                return Result.success("暂时还没有单词了");
            }
            return Result.success(englishs);
        } catch (InvalidWordRangeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加错词 -- 在原数据标记
     */
    @PostMapping("/notGrasp/{id}")
    public Result addAgainWord(@PathVariable int id){  // 未掌握单词，在数据库中标记该字段即可，同时错误次数加一
        System.out.println("插入操作已触发");
        boolean result = englishService.setNotGrasp(id);
        return result ? Result.success() : Result.error("添加失败");
    }

    /**
     * 标记已掌握，将单词数据表字段改为已掌握 -- 数据库会触发触发器，将该单词的数据插入错词二次复习表中
     */
    @PostMapping("/isGrasp/{id}")
    public Result isGrasp(@PathVariable int id){
        System.out.println("删除操作已触发");
        boolean result = englishService.isGrasp(id);
        return result ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取错词列表 -- 错词本页面使用
     */
    @PostMapping("/errorWordList")
    public Result getErrorWordList(@RequestBody WordRequest request) {
        System.out.println("获取错词列表已触发");
        List<Englishs> errorWords = englishService.getErrorWordList(request);
        if (errorWords == null || errorWords.isEmpty()) {
            return Result.success("你很棒哦，没有错误单词");
        }
        return Result.success(errorWords);
    }

    /**
     * 获取用户学习统计数据
     */
    @GetMapping("/stats/{userId}")
    public Result getUserStats(@PathVariable int userId, @RequestParam(required = false) Integer bookId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            int totalWords;
            int masteredWords;
            int errorWords;

            if (bookId != null) {
                List<Englishs> allWords = englishMapper.getAllWordsByUserAndBook(userId, bookId);
                totalWords = allWords.size();
                masteredWords = (int) allWords.stream().filter(w -> w.getIsGrasp() != null && w.getIsGrasp() == 1).count();
                errorWords = (int) allWords.stream().filter(w -> w.getIsGrasp() != null && w.getIsGrasp() == 2).count();
            } else {
                totalWords = englishMapper.countTotalWords(userId);
                masteredWords = englishMapper.countMasteredWords(userId);
                errorWords = englishMapper.countErrorWords(userId);
            }

            stats.put("totalWords", totalWords);
            stats.put("masteredWords", masteredWords);
            stats.put("errorWords", errorWords);

            List<Sentence> allSentences = englishMapper.getAllSentencesByUser(userId);
            stats.put("totalSentences", allSentences.size());
            stats.put("masteredSentences", allSentences.stream().mapToInt(s -> s.getIsGrasp() == 1 ? 1 : 0).sum());
            stats.put("errorSentences", allSentences.stream().mapToInt(s -> s.getIsGrasp() == 2 ? 1 : 0).sum());

            // 今日统计始终跨所有课本，不限制特定课本
            stats.put("todayMasteredWords", englishMapper.countTodayMasteredWords(userId, null));
            stats.put("todayErrorWords", englishMapper.countTodayErrorWords(userId, null));
            stats.put("studyDays", studyStatMapper.countStudyDays(userId));

            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取句子列表
     */
    @PostMapping("/sentence")
    public Result getSentenceList(@RequestBody WordRequest sentenceRequest){  // 此处用单词的request，但是只接受userId，start，end三个参数，表中没有bookId
        System.out.println("获取句子列表已触发");
        List<Sentence> sentenceList = englishService.getSentenceList(sentenceRequest);
        if (sentenceList == null || sentenceList.isEmpty()) {
            return Result.success("你很懒哦，暂时还没有句子");
        }
        return Result.success(sentenceList);
    }

    /**
     * 获取错误句子列表
     */
    @GetMapping("/errorSentence/{userId}")
    public Result getErrorSentence(@PathVariable int userId){
        System.out.println("获取错误句子已触发");
        List<Sentence> errorSentence = englishService.getErrorSentence(userId);
        if (errorSentence == null || errorSentence.isEmpty()) {
            return Result.success("你很棒，没有错误句子");
        }
        return Result.success(errorSentence);
    }


    /**
     * 添加错误句子/标记未掌握/标记错句 -- 在原数据标记
     */
    @PostMapping("/sentenceNotGrasp/{id}")
    public Result sentenceNotGrasp(@PathVariable int id){
        System.out.println("错句登记操作已触发");
        boolean result = englishService.sentenceNotGrasp(id);
        return result ? Result.success() : Result.error("添加失败");
    }

    /**
     * 句子已掌握
     */
    @PostMapping("/sentenceIsGrasp/{id}")
    public Result sentenceIsGrasp(@PathVariable int id){     // TODO 还未添加触发器 2026.01.07 09：43
        System.out.println("触发标记句子已掌握");
        boolean result = englishService.sentenceIsGrasp(id);
        return result ? Result.success() : Result.error("删除失败");
    }

    /**
     * 更新单词中文释义
     */
    @PostMapping("/word/{id}/chinese")
    public Result updateChineseMeaning(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String chinese = body.get("chinese");
        if (chinese == null || chinese.trim().isEmpty()) {
            return Result.error("中文释义不能为空");
        }
        boolean updated = englishService.updateChineseMeaning(id, chinese.trim());
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }
}
