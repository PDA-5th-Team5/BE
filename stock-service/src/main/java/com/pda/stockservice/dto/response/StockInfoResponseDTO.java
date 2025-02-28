package com.pda.stockservice.dto.response;

import com.pda.stockservice.entity.Market;
import com.pda.stockservice.entity.Sectors;
import com.pda.stockservice.entity.Stock;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockInfoResponseDTO {
    private StockInfoDTO stockInfo;
    private SnowflakeSDTO snowflakeS;

    @Builder
    @Getter
    public static class StockInfoDTO {
        private Short stockId;
        private String companyName;
        private Market market;
        private Integer currentPrice;
        private Integer OneWeekProfitRate;
        private Integer OneYearProfitRate;
        private Sectors sectors;
        private String companyOverview;

        private Double eps;
        private Double bps;
        private Double pbr;
    }

    @Builder
    @Getter
    public static class SnowflakeSDTO {
        private Double per;
        private Double lbltRate;
        private Long marketCap;
        private Double dividendYield;
        private Double foreignerRatio;
    }



    public static StockInfoResponseDTO toDTO(Stock stock) {
        return StockInfoResponseDTO.builder()
                .stockInfo(StockInfoDTO.builder()
                        .stockId(stock.getStockId())
                        .companyName(stock.getCompanyName())
                        .market(stock.getMarket())
//                        .currentPrice(stock.getCurrentPrice())
//                        .OneWeekProfitRate(stock.getOneWeekProfitRate())
//                        .OneYearProfitRate(stock.getOneYearProfitRate())
                        .sectors(stock.getSectors())
                        .companyOverview(stock.getCompanyOverview())
                        .eps(stock.getEps())
                        .bps(stock.getBps())
                        .pbr(stock.getPbr())
                        .build())
                .snowflakeS(SnowflakeSDTO.builder()
                        .per(stock.getPer())
                        .lbltRate(stock.getLbltRate())
                        .marketCap(stock.getMarketCap())
                        .dividendYield(stock.getDividendYield())
                        .foreignerRatio(stock.getForeignerRatio())
                        .build())
                .build();
    }
}
