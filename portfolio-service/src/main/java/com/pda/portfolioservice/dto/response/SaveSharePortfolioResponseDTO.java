package com.pda.portfolioservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SaveSharePortfolioResponseDTO {
    private Long myPortfolioId;
}
