package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.entity.MyPortfolio;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PortfolioSummaryResponseDTO {

    private int avgMarketCap;
    private double avgPer;
    private double avgDebt;
    private double avgDividend;

    public static PortfolioSummaryResponseDTO toDTO(MyPortfolio myPortfolio) {
        return PortfolioSummaryResponseDTO.builder()
//                .avgMarketCap()
//                .avgPer()
//                .avgDebt()
//                .avgDividend
                .build();
    }
}
