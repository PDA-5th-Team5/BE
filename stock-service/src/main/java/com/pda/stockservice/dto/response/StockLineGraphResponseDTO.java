package com.pda.stockservice.dto.response;

import com.pda.stockservice.enums.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockLineGraphResponseDTO {

    private LineGraphDTO lineGraph;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketIndicatorGraphResponseDTO {
        private Market market;
        private Map<String, Float> price;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockGraphResponseDTO {
        private String companyName;
        private Map<String, Float> closePrice;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineGraphDTO {
        private List<MarketIndicatorGraphResponseDTO> marketGraph;
        private StockGraphResponseDTO stockGraph;
    }
}
