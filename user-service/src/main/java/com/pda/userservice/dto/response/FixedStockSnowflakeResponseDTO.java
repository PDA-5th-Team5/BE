package com.pda.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FixedStockSnowflakeResponseDTO {
    private Byte per;
    private Byte lbltRate;
    private Byte marketCap;
    private Byte divYield;
    private Byte foreignerRatio;
}