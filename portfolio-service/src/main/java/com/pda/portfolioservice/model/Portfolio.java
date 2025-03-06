package com.pda.portfolioservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pda.portfolioservice.dto.request.StockFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "portfolios")
@CompoundIndexes({
        @CompoundIndex(name = "category_portfolio_idx", def = "{'category': 1, 'portfolioId': 1}", unique = true)
})
public class Portfolio {
    @Id
    private String id;  // MongoDB 기본 _id (사용하지 않을 수도 있음)
    private String title;
    private String description;
    private String category;
    private Long portfolioId;
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



    public StockFilter toStockFilter() {
        StockFilter stockFilter = new StockFilter();
        stockFilter.setMarketCap(this.marketCap);
        stockFilter.setPer(this.per);
        stockFilter.setEps(this.eps);
        stockFilter.setBps(this.bps);
        stockFilter.setPbr(this.pbr);
        stockFilter.setDividendYield(this.dividendYield);
        stockFilter.setForeignerRatio(this.foreignerRatio);
        stockFilter.setSps(this.sps);
        stockFilter.setSaleAccount(this.saleAccount);
        stockFilter.setCrntRate(this.crntRate);
        stockFilter.setLbltRate(this.lbltRate);
        stockFilter.setNtinInrt(this.ntinInrt);
        stockFilter.setBsopPrfiInrt(this.bsopPrfiInrt);
        stockFilter.setGrs(this.grs);
        stockFilter.setRoeVal(this.roeVal);
        stockFilter.setBsopPrti(this.bsopPrti);
        stockFilter.setThtrNtin(this.thtrNtin);
        return stockFilter;
    }
}