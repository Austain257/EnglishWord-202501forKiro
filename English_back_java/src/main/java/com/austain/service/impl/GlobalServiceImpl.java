package com.austain.service.impl;

import com.austain.domain.dto.MotivationQuoteResult;
import com.austain.mapper.GlobalMapper;
import com.austain.service.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalServiceImpl implements GlobalService {

    @Autowired
    private GlobalMapper globalMapper;

    @Override
    public List<MotivationQuoteResult> getQuoteList() {
        List<MotivationQuoteResult> quoteAllList = globalMapper.getQuoteAllLisst();

        // 根据文案总数生成5个随机数，用来返回随机文案
        int[] randomNum = new int[5];
        for (int i = 0; i < 5; i++) {
            randomNum[i] = (int)(Math.random() * quoteAllList.size());
        }
        List<MotivationQuoteResult> resultList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            resultList.add(quoteAllList.get(randomNum[i]));
        }
        return resultList;
    }
}
