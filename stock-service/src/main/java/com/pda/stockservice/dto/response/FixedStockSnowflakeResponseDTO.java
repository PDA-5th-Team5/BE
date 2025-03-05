package com.pda.stockservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FixedStockSnowflakeResponseDTO {
    private Byte per;
    private Byte lbltRate;
    private Byte marketCap;
    private Byte divYield;
    private Byte foreignerRatio;
}