package com.pda.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentsResponseDTO {
    private List<StockCommentResponseDTO> commentsS;
    private List<PortfolioCommentResponseDTO> commentsP;
}
