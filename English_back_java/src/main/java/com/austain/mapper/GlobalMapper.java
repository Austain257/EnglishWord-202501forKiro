package com.austain.mapper;


import com.austain.domain.dto.MotivationQuoteResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GlobalMapper {

    @Select("SELECT * FROM motivation_quotes WHERE status = 1 ORDER BY priority DESC")
    List<MotivationQuoteResult> getAllQuotes();
    
    @Select("SELECT * FROM motivation_quotes")
    List<MotivationQuoteResult> getQuoteAllLisst();
}
