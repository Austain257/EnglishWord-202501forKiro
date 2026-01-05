package com.austain.domain.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordRequest {

    private int userId;
    private int bookId;
    private int start;
    private int end;
}
