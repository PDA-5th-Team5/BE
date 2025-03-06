package com.pda.stockservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PortfolioSummaryResponseDTO {

    private int avgMarketCap;
    private double avgPer;
    private double avgDebt;
    private double avgDividend;

}
