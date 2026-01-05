package com.austain.controller;


import com.austain.domain.dto.MotivationQuoteResult;
import com.austain.domain.dto.Result;
import com.austain.srevice.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/global")
public class GlobalController {

    @Autowired
    private GlobalService globalService;

    @GetMapping("/motivation/list")
    public Result getMotivationQuoteList(){
        System.out.println("后端获取激励文案列表已触发~");

        // TODO service里面用的是查询数据库所有文案返回再挑选，如果确定不增加文案到数据库里面，其实可以写死的，传入随机id查可能会更快
        List<MotivationQuoteResult> quoteAllList = globalService.getQuoteList();

        if (quoteAllList == null || quoteAllList.isEmpty()){
            return Result.error("没有激励文案~");
        }
        return Result.success(quoteAllList);
    }
}
