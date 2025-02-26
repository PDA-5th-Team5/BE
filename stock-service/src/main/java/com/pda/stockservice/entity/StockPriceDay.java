package com.pda.stockservice.entity;

import jakarta.persistence.*;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPriceDay {

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class StockPriceDayId implements Serializable {
        private LocalDate date;
        private Short stockId;
    }

    @EmbeddedId
    private StockPriceDayId id;

    @MapsId("stockId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    private int openPrice;
    private int highPrice;
    private int lowPrice;
    private int closePrice;
    private Long volume;
    private double changeRate;
}