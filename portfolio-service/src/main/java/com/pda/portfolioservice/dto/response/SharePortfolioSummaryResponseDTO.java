package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.entity.MyPortfolio;
import com.pda.portfolioservice.entity.SharePortfolio;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SharePortfolioSummaryResponseDTO {

    private int avgMarketCap;
    private double avgPer;
    private double avgDebt;
    private double avgDividend;

    public static SharePortfolioSummaryResponseDTO toDTO(SharePortfolio sharePortfolio) {
        return SharePortfolioSummaryResponseDTO.builder()
//                .avgMarketCap()
//                .avgPer()
//                .avgDebt()
//                .avgDividend
                .build();
    }
}
