package com.pda.stockservice.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketIndicatorPrice {

    public static class MarketIndicatorPriceId implements Serializable {
        private String Market;
        private LocalDate date;
    }

    @EmbeddedId
    private MarketIndicatorPriceId id;
    private int price;


}
