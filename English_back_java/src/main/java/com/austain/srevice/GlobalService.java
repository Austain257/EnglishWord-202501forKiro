package com.austain.srevice;

import com.austain.domain.dto.MotivationQuoteResult;

import java.util.List;

public interface GlobalService {
    List<MotivationQuoteResult> getQuoteList();
}
