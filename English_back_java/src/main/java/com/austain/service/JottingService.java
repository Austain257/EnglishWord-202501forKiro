package com.austain.service;

import com.austain.domain.dto.PageResult;
import com.austain.domain.po.Jotting;
import com.austain.domain.po.PageRequest;

import java.util.List;

public interface JottingService {
    PageResult<Jotting> getList(PageRequest pageRequest);

    int addJotting(Jotting jotting);

    int updateJotting(Jotting jotting);

    int deleteJotting(int id, int userId);

    int batchDeleteJotting(List<Jotting> jottingList);

    int setReview(int userId, int id);
}
