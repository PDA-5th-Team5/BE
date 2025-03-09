package com.pda.stockservice.dto.response;

import com.pda.stockservice.enums.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioMarketGraphResponseDTO {

    private LineGraphDTO lineGraph;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineGraphDTO {
        private Market market;
        private Map<String, Float> price;
        private String portfolioTitle;
        private Map<String, Float> avgClosePrice;
    }
}
