package com.austain.mapper;

import com.austain.domain.po.EnglishWord02;
import com.austain.domain.po.UserBook;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBookMapper {

    // 添加课本 -- 更新课本表
    int insert(UserBook userBook);

    // 批量添加单词
    int insertBatch(@Param("list") List<EnglishWord02> list);
}
