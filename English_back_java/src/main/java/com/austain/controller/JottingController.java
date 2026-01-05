package com.austain.controller;

import com.austain.domain.dto.PageResult;
import com.austain.domain.dto.Result;
import com.austain.domain.po.Jotting;
import com.austain.domain.po.PageRequest;
import com.austain.srevice.JottingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jotting")
public class JottingController {

    @Autowired
    private JottingService jottingService;

    @GetMapping("/list")
    public Result getList(@RequestParam int userId, 
                         @RequestParam(defaultValue = "1") int page, 
                         @RequestParam(defaultValue = "10") int size){
        System.out.println("获取积累本列表已触发");
        PageRequest pageRequest = new PageRequest();
        pageRequest.setUserId(userId);
        pageRequest.setPage(page);
        pageRequest.setSize(size);
        
        PageResult<Jotting> jpageResult = jottingService.getList(pageRequest);
        return Result.success(jpageResult);
    }

    @PostMapping("/add")
    public Result addJotting(@RequestBody Jotting jotting){
        System.out.println("积累本添加操作已触发");
        int result = jottingService.addJotting(jotting);
        return result > 0 ? Result.success() : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result updateJotting(@RequestBody Jotting jotting){
        System.out.println("积累本更新操作已触发");
        int result = jottingService.updateJotting(jotting);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    @PostMapping("/setReview/{userId}")
    public Result setReview(@PathVariable int userId, @RequestParam int id){
        System.out.println("设积累本置已复习操作已触发");
        int result = jottingService.setReview(userId,id);
        return result > 0 ? Result.success() : Result.error("标记失败");
    }

    @DeleteMapping("/delete")
    public Result deleteJotting(@RequestBody Jotting jottingRequest){  // 由于id和userId在路径中传输不安全，故用对象接收，此参数只接受id和userId
        System.out.println("积累本删除操作已触发");
        int result = jottingService.deleteJotting(jottingRequest.getId(),jottingRequest.getUserId());
        return result > 0 ? Result.success() : Result.error("删除失败");
    }

    /**
     * 批量删除
     */
    @PostMapping("/batchDelete")  // TODO 注意jotting表中id用的是int，但是ids用的是String
    public Result batchDeleteJotting(@RequestBody List<Jotting> jottingList){
        System.out.println("积累本批量删除操作已触发");
        int result = jottingService.batchDeleteJotting(jottingList);
        return result > 0 ? Result.success() : Result.error("批量删除失败");
    }
}
