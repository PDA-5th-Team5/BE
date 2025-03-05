package com.pda.portfolioservice.dto.request;

import com.pda.portfolioservice.model.Portfolio;
import com.pda.portfolioservice.model.Range;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioRequestDTO {
    private String title;
    private String description;
    private String category;
    private String market;
    private List<String> sector;
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

    public Portfolio toEntity() {
        Portfolio portfolio = new Portfolio();
        portfolio.setTitle(this.title);
        portfolio.setDescription(this.description);
        portfolio.setCategory(this.category);
        portfolio.setSector(this.sector);
        portfolio.setMarket(this.market);
        portfolio.setMarketCap(this.marketCap);
        portfolio.setPer(this.per);
        portfolio.setEps(this.eps);
        portfolio.setBps(this.bps);
        portfolio.setPbr(this.pbr);
        portfolio.setDividendYield(this.dividendYield);
        portfolio.setForeignerRatio(this.foreignerRatio);
        portfolio.setSps(this.sps);
        portfolio.setSaleAccount(this.saleAccount);
        portfolio.setCrntRate(this.crntRate);
        portfolio.setLbltRate(this.lbltRate);
        portfolio.setNtinInrt(this.ntinInrt);
        portfolio.setBsopPrfiInrt(this.bsopPrfiInrt);
        portfolio.setGrs(this.grs);
        portfolio.setRoeVal(this.roeVal);
        portfolio.setBsopPrti(this.bsopPrti);
        portfolio.setThtrNtin(this.thtrNtin);
        return portfolio;
    }
}