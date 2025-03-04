package com.pda.stockservice.dto.response;
import lombok.*;
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {
    private Short stockId; // `stock_id`
    private String ticker; // `ticker`
    private String marketType; // `market_type`
    private String companyName; // `company_name`
    private String sector; // `sector`
    private String companyOverview; // `company_overview`
    private Long marketCap; // `market_cap`
    private Long thtrNtin; // `thtr_ntin`
    private Long bsopPrti; // `bsop_prti`
    private Double per; // `per`
    private Double eps; // `eps`
    private Double bps; // `bps`
    private Double pbr; // `pbr`
    private Double dividendYield; // `dividend_yield`
    private Double foreignerRatio; // `foreigner_ratio`
    private Double sps; // `sps`
    private Long saleAccount; // `sale_account`
    private Double crntRate; // `crnt_rate`
    private Double lbltRate; // `lblt_rate`
    private Double ntinInrt; // `ntin_inrt`
    private Double bsopPrfiInrt; // `bsop_prfi_inrt`
    private Double grs; // `grs`
    private Double roeVal; // `roe_val`
    private Double weekRateChange;  // `week_rate_change`
    private Double yearRateChange;  // `year_rate_change`
    private Integer currentPrice;
    private Double changeRate;
}