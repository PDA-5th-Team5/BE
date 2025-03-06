package com.pda.userservice.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MyPortfolioCommentsResponseDTO {
    private List<PortfolioCommentResponseDTO> commentsP;
}

@Getter
class PortfolioCommentResponseDTO {
    private Long connectId;  // stock_id
    private String name;     // 종목명
    private Long commentId;
    private String content;
    private String date;
}