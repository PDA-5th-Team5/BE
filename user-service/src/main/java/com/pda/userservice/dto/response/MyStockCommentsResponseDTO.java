package com.pda.userservice.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MyStockCommentsResponseDTO {
    private List<StockCommentResponseDTO> commentsS;
}

@Getter
class StockCommentResponseDTO {
    private Short connectId;  // stock_id
    private String name;     // 종목명
    private Long commentId;
    private String content;
    private String date;
}