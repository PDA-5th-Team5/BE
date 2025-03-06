package com.pda.stockservice.dto.response;

import com.pda.stockservice.entity.StockStat;
import com.pda.stockservice.enums.Market;
import com.pda.stockservice.entity.Stock;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StockInfoResponseDTO {
    private StockInfoDTO stockInfo;
    private SnowflakeSDTO snowflakeS;

    @Builder
    @Getter
    @Setter
    public static class StockInfoDTO {
        private Short stockId;
        private String companyName;
        private Market marketType;
        private Integer currentPrice;
        private Integer OneWeekProfitRate;
        private Integer OneYearProfitRate;
        private String sector;
        private String companyOverview;

        private Double eps;
        private Double bps;
        private Double pbr;
    }

    @Builder
    @Getter
    public static class SnowflakeSDTO {
        private Byte per;
        private Byte lbltRate;
        private Byte marketCap;
        private Byte dividendYield;
        private Byte foreignerRatio;
    }



    public static StockInfoResponseDTO toDTO(Stock stock, StockStat stockStat) {
        return StockInfoResponseDTO.builder()
                .stockInfo(StockInfoDTO.builder()
                        .stockId(stock.getStockId())
                        .companyName(stock.getCompanyName())
                        .marketType(stock.getMarketType())
//                        .currentPrice(stock.getCurrentPrice())
//                        .OneWeekProfitRate(stock.getOneWeekProfitRate())
//                        .OneYearProfitRate(stock.getOneYearProfitRate())
                        .sector(stock.getSector())
                        .companyOverview(stock.getCompanyOverview())
                        .eps(stock.getEps())
                        .bps(stock.getBps())
                        .pbr(stock.getPbr())
                        .build())
                .snowflakeS(SnowflakeSDTO.builder()
                        .per(stockStat.getPer())
                        .lbltRate(stockStat.getLbltRate())
                        .marketCap(stockStat.getMarketCap())
                        .dividendYield(stockStat.getDividendYield())
                        .foreignerRatio(stockStat.getForeignerRatio())
                        .build())
                .build();
    }
}
