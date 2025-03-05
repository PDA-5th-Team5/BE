package com.pda.stockservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyStockWatchlistResponseDTO {
    private Short stockId;
    private String ticker;
    private String marketType;
    private String companyName;
    private String sector;
    private String companyOverview;
    private Long marketCap;
    private Long bsopPrti;
    private double per;
    private double bps;
    private Double weekRateChange;
    private Double yearRateChange;
    private FixedStockSnowflakeResponseDTO snowflakeS; // 추가된 필드
}
