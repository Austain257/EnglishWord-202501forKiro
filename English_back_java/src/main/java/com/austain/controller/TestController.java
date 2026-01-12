package com.austain.controller;

// TODO
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
public class TestController {
/*
    private final ChatClient textClient;

    // 通过构造函数注入ChatClient
    public TestController(ChatClient textClient) {
        this.textClient = textClient;
    }

    @Autowired
    private EnglishService englishService;

    @GetMapping(value = "/generation", produces = "text/html;charset=utf-8")
    public Flux<String> generateText(
            @RequestParam("beginIndex") String beginIndex,
            @RequestParam("endIndex") String endIndex,
            @RequestParam("bookName") String bookName) {

        System.out.println("开始查数据库，当前需求为" + beginIndex + "到" + endIndex);
        List<Englishs> selectedWords = englishService.getEnglishList(beginIndex, endIndex,bookName);
        System.out.println("开始生成文章");

        return textClient.prompt()
//                .user("article ache astronaut without acid specify daylight poetry seven fetch immigrant")
                .user(selectedWords.toString())
                .stream()
                .content();
    }*/
}