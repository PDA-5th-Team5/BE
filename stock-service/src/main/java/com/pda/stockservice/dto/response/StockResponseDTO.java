package com.pda.stockservice.dto.response;

import com.pda.stockservice.enums.Market;
import lombok.Getter;

@Getter
public class StockResponseDTO {
    private Short stockId;
    private String ticker;
    private Market market;
    private String sectors;
    private String companyName;
    private String sector;
    private String companyOverview;
    private Long marketCap;
    private Double per;
    private Double pbr;
    private Double dividendYield;
    private double roeVal;

    public StockResponseDTO(Short stockId, String ticker, Market market, String sectors,
                    String companyName, String sector, String companyOverview,
                    Long marketCap, Double per, Double pbr, Double dividendYield, double roeVal) {
        this.stockId = stockId;
        this.ticker = ticker;
        this.market = market;
        this.sectors = sectors;
        this.companyName = companyName;
        this.sector = sector;
        this.companyOverview = companyOverview;
        this.marketCap = marketCap;
        this.per = per;
        this.pbr = pbr;
        this.dividendYield = dividendYield;
        this.roeVal = roeVal;
    }
}