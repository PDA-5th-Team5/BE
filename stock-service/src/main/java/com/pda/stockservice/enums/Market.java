package com.pda.stockservice.enums;

import java.util.Arrays;
import java.util.List;

public enum Market {
    ALL, KOSPI, KOSDAQ;

    public static List<Market> getMarkets(Market market) {
        if (market == ALL) {
            return Arrays.asList(KOSPI, KOSDAQ);
        }
        return List.of(market);
    }
}
