package com.pda.stockservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ThresholdsResponseDTO {
    private List<Double> pbr;
    private List<Double> ntinInrt;
    private List<Double> bps;
    private List<Double> roeVal;
    private List<Double> crntRate;
    private List<Double> saleAccount;
    private List<Double> grs;
    private List<Double> eps;
    private List<Double> bsopPrfiInrt;
    private List<Double> marketCap;
    private List<Double> lbltRate;
    private List<Double> sps;
    private List<Double> foreignerRatio;
    private List<Double> dividendYield;
    private List<Double> per;
    private List<Double> thtrNtin;
    private List<Double> bsopPrti;
}
