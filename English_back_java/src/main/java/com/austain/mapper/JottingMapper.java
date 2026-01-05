package com.austain.mapper;

import com.austain.domain.po.Jotting;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JottingMapper {

    // 分页查询笔记列表 - 移除type参数，因为前端会自己过滤
    // 只能查自己的，管理员的也不能查，因为是自己积累的
    @Select("select * from jottings where user_id = #{userId} order by create_time desc")
    List<Jotting> page(int userId);

    // 添加笔记
    int addJotting(Jotting jotting);

    // 更新笔记
    int updateJotting(Jotting jotting);

    // 设置笔记为已复习
    @Update("UPDATE jottings SET reviewed = 1 WHERE user_id = #{userId} AND id = #{id}")
    int setReview(int userId, int id);

    // 删除笔记
    @Delete("DELETE FROM jottings WHERE user_id = #{userId} AND id = #{id}")
    int deleteJotting(int id, int userId);

    // 批量删除笔记
    int batchDeleteJotting(List<String> ids);

}
