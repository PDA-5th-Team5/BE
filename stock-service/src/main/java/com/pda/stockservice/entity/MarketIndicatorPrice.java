package com.pda.stockservice.entity;

import com.pda.stockservice.enums.Market;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketIndicatorPrice {


    @Embeddable
    @Getter
    public static class MarketIndicatorPriceId implements Serializable {

        @Enumerated(EnumType.STRING)
        private Market market;
        private LocalDate date;
    }

    @EmbeddedId
    private MarketIndicatorPriceId id;
    private int price;


}
