package com.pda.stockservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SnowflakeDTO {
    private Short stockId;
    private Short marketCap;
    private Short per;
    private Short eps;
    private Short bps;
    private Short pbr;
    private Short dividendYield;
    private Short foreignerRatio;
    private Short sps;
    private Short saleAccount;
    private Short crntRate;
    private Short lbltRate;
    private Short ntinInrt;
    private Short bsopPrfiInrt;
    private Short grs;
    private Short roeVal;
    private Short bsopPrti;
    private Short thtrNtin;

    public static SnowflakeDTO filterSnowflake(SnowflakeDTO original, StockFilter filters) {
        SnowflakeDTO filtered = new SnowflakeDTO();

        if (filters.getMarketCap() != null) filtered.setMarketCap(original.getMarketCap());
        if (filters.getPer() != null) filtered.setPer(original.getPer());
        if (filters.getEps() != null) filtered.setEps(original.getEps());
        if (filters.getBps() != null) filtered.setBps(original.getBps());
        if (filters.getPbr() != null) filtered.setPbr(original.getPbr());
        if (filters.getDividendYield() != null) filtered.setDividendYield(original.getDividendYield());
        if (filters.getForeignerRatio() != null) filtered.setForeignerRatio(original.getForeignerRatio());
        if (filters.getSps() != null) filtered.setSps(original.getSps());
        if (filters.getSaleAccount() != null) filtered.setSaleAccount(original.getSaleAccount());
        if (filters.getCrntRate() != null) filtered.setCrntRate(original.getCrntRate());
        if (filters.getLbltRate() != null) filtered.setLbltRate(original.getLbltRate());
        if (filters.getNtinInrt() != null) filtered.setNtinInrt(original.getNtinInrt());
        if (filters.getBsopPrfiInrt() != null) filtered.setBsopPrfiInrt(original.getBsopPrfiInrt());
        if (filters.getGrs() != null) filtered.setGrs(original.getGrs());
        if (filters.getRoeVal() != null) filtered.setRoeVal(original.getRoeVal());
        if (filters.getBsopPrti() != null) filtered.setBsopPrti(original.getBsopPrti());
        if (filters.getThtrNtin() != null) filtered.setThtrNtin(original.getThtrNtin());

        return filtered;
    }
}