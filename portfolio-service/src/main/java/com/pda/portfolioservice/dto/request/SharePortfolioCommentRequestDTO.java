package com.pda.portfolioservice.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SharePortfolioCommentRequestDTO {
    private String content;
}
