package com.pda.stockservice.dto.response;

import com.pda.stockservice.entity.StockPriceDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandleResponseDTO {
    private List<CandleDTO> candles;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandleDTO {
        private LocalDate date;
        private int openPrice;
        private int highPrice;
        private int lowPrice;
        private int closePrice;
        private Long volume;
    }

    public static CandleResponseDTO toDTO(List<StockPriceDay> stockPriceDay){
        List<CandleDTO> candles = stockPriceDay.stream()
                .map(spd -> CandleDTO.builder()
                        .date(spd.getId().getDate())
                        .openPrice(spd.getOpenPrice())
                        .highPrice(spd.getHighPrice())
                        .lowPrice(spd.getLowPrice())
                        .closePrice(spd.getClosePrice())
                        .volume(spd.getVolume())
                        .build())
                .collect(Collectors.toList());


        return CandleResponseDTO.builder()
                .candles(candles)
                .build();

    }
}
