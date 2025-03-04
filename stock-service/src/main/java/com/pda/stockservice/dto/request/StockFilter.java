package com.pda.stockservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockFilter {
    private Range marketCap;
    private Range per;
    private Range eps;
    private Range bps;
    private Range pbr;
    private Range dividendYield;
    private Range foreignerRatio;
    private Range sps;
    private Range saleAccount;
    private Range crntRate;
    private Range lbltRate;
    private Range ntinInrt;
    private Range bsopPrfiInrt;
    private Range grs;
    private Range roeVal;
    private Range bsopPrti;
    private Range thtrNtin;

    @Getter
    @Setter
    public static class Range {
        private Double min;
        private Double max;
    }
}