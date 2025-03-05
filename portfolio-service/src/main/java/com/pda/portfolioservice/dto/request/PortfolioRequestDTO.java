package com.pda.portfolioservice.dto.request;

import com.pda.portfolioservice.model.Portfolio;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioRequestDTO {
    private String title;
    private String description;
    private String category;
    private String market;
    private List<String> sector;
    private Portfolio.Range marketCap;
    private Portfolio.Range per;
    private Portfolio.Range eps;
    private Portfolio.Range bps;
    private Portfolio.Range pbr;
    private Portfolio.Range dividendYield;
    private Portfolio.Range foreignerRatio;
    private Portfolio.Range sps;
    private Portfolio.Range saleAccount;
    private Portfolio.Range crntRate;
    private Portfolio.Range lbltRate;
    private Portfolio.Range ntinInrt;
    private Portfolio.Range bsopPrfiInrt;
    private Portfolio.Range grs;
    private Portfolio.Range roeVal;
    private Portfolio.Range bsopPrti;
    private Portfolio.Range thtrNtin;

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