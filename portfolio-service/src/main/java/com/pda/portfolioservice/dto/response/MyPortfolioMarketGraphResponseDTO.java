package com.pda.portfolioservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pda.portfolioservice.enums.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyPortfolioMarketGraphResponseDTO {

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
