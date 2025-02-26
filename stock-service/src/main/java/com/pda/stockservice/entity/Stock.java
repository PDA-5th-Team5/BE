package com.pda.stockservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short stockId;

    private String ticker;
    private String marketType;
    private String companyName;
    private String sector;
    private String companyOverview;
    private Long marketCap;
    private Long thtrNtin;
    private Long bsopPrti;
    private double per;
    private double eps;
    private double bps;
    private double pbr;
    private double dividendYield;
    private double foreignerRatio;
    private double sps;
    private Long saleAccount;
    private double crntRate;
    private double lbltRate;
    private double ntinInrt;
    private double bsopPrfiInrt;
    private double grs;
    private double roeVal;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<StockComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<StockPriceDay> stockPriceDays = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<FavoriteStock> favoriteStocks = new ArrayList<>();
}
