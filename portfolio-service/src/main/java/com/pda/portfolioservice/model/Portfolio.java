package com.pda.portfolioservice.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
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

    @Getter
    @Setter
    public static class Range {
        private Double min;
        private Double max;
    }
}