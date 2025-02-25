package com.pda.stockservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockStat {
    @Id
    private short stockId;

    private String ticker;
    private String companyName;
    private String marketType;
    private String sector;
    private String companyOverview;
    private Byte marketCap;
    private Byte per;
    private Byte eps;
    private Byte bps;
    private Byte pbr;
    private Byte dividendYield;
    private Byte foreignerRatio;
    private Byte sps;
    private Byte saleAccount;
    private Byte crntRate;
    private Byte lbltRate;
    private Byte ntinInrt;
    private Byte bsopPrfiInrt;
    private Byte grs;
    private Byte roeVal;
    private Byte bsopPrti;
    private Byte thtrNtin;


}
