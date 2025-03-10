package com.pda.stockservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pda.stockservice.enums.Market;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값 자동제거
public class StockLineGraphResponseDTO {

    private List<LineGraphDTO> lineGraph;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LineGraphDTO {
        private Market market;
        private String companyName;
        private Map<String, Float> price;
        private Map<String, Float> closePrice;
    }
}
