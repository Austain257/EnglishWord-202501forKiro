package com.austain.srevice.impl;

import com.austain.domain.dto.PageResult;
import com.austain.domain.po.Jotting;
import com.austain.domain.po.PageRequest;
import com.austain.mapper.JottingMapper;
import com.austain.srevice.JottingService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JottingServiceImpl implements JottingService {

    @Autowired
    private JottingMapper jottingMapper;

    @Override
    public PageResult<Jotting> getList(PageRequest request) {
        PageHelper.startPage(request.getPage(), request.getSize());  // 开启分页,其后必须紧接查询语句才有效
        List<Jotting> list = jottingMapper.page(request.getUserId());  // 获取分页结果，移除type参数
        Page<Jotting> pageList = (Page<Jotting>) list;
        return new PageResult<>(pageList.getTotal(),pageList.getResult());
    }

    @Override
    public int addJotting(Jotting jotting) {
        return jottingMapper.addJotting(jotting);
    }

    @Override
    public int updateJotting(Jotting jotting) {
        return jottingMapper.updateJotting(jotting);
    }

    @Override
    public int setReview(int userId, int id) {
        return jottingMapper.setReview(userId, id);
    }

    @Override
    public int deleteJotting(int id, int userId) {
        return jottingMapper.deleteJotting(id, userId);
    }

    @Override
    public int batchDeleteJotting(List<Jotting> jottingList) {
        List<String> ids = jottingList.stream().map(Jotting::getId).map(String::valueOf).toList();
        return jottingMapper.batchDeleteJotting(ids);
    }
}
