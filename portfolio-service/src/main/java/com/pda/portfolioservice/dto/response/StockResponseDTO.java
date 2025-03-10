package com.pda.portfolioservice.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pda.portfolioservice.dto.request.SnowflakeDTO;
import com.pda.portfolioservice.dto.request.StockFilter;
import lombok.*;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockResponseDTO {
    private Short stockId;
    private String ticker;
    private String marketType;
    private String companyName;
    private String sector;
    private String companyOverview;
    private SnowflakeDTO snowflakeS;
    private Long marketCap;
    private Long thtrNtin;
    private Long bsopPrti;
    private Double per;
    private Double eps;
    private Double bps;
    private Double pbr;
    private Double dividendYield;
    private Double foreignerRatio;
    private Double sps;
    private Long saleAccount;
    private Double crntRate;
    private Double lbltRate;
    private Double ntinInrt;
    private Double bsopPrfiInrt;
    private Double grs;
    private Double roeVal;
    private Double weekRateChange;
    private Double yearRateChange;
    private Integer currentPrice;
    private Double changeRate;
    private Boolean fav;

    public static StockResponseDTO filterStockResponse(StockResponseDTO stock, StockFilter filters) {
        return StockResponseDTO.builder()
                .stockId(stock.getStockId())
                .ticker(stock.getTicker())
                .marketType(stock.getMarketType())
                .companyName(stock.getCompanyName())
                .sector(stock.getSector())
                .companyOverview(stock.getCompanyOverview())
                .snowflakeS(SnowflakeDTO.filterSnowflake(stock.getSnowflakeS(), filters))
                .marketCap(stock.getMarketCap())
                .lbltRate(stock.getLbltRate())
//                .marketCap(filters.getMarketCap() != null ? stock.getMarketCap() : null)
                .thtrNtin(filters.getThtrNtin() != null ? stock.getThtrNtin() : null)
                .bsopPrti(filters.getBsopPrti() != null ? stock.getBsopPrti() : null)
                .per(filters.getPer() != null ? stock.getPer() : null)
                .eps(filters.getEps() != null ? stock.getEps() : null)
                .bps(filters.getBps() != null ? stock.getBps() : null)
                .pbr(filters.getPbr() != null ? stock.getPbr() : null)
                .dividendYield(filters.getDividendYield() != null ? stock.getDividendYield() : null)
                .foreignerRatio(filters.getForeignerRatio() != null ? stock.getForeignerRatio() : null)
                .sps(filters.getSps() != null ? stock.getSps() : null)
                .saleAccount(filters.getSaleAccount() != null ? stock.getSaleAccount() : null)
                .crntRate(filters.getCrntRate() != null ? stock.getCrntRate() : null)
                .ntinInrt(filters.getNtinInrt() != null ? stock.getNtinInrt() : null)
                .bsopPrfiInrt(filters.getBsopPrfiInrt() != null ? stock.getBsopPrfiInrt() : null)
                .grs(filters.getGrs() != null ? stock.getGrs() : null)
                .roeVal(filters.getRoeVal() != null ? stock.getRoeVal() : null)
                .weekRateChange(stock.getWeekRateChange())
                .yearRateChange(stock.getYearRateChange())
                .currentPrice(stock.getCurrentPrice())
                .changeRate(stock.getChangeRate())
                .fav(stock.getFav())
                .build();
    }


}