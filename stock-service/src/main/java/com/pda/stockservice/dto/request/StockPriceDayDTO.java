package com.pda.stockservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class StockPriceDayDTO {
    private Short stockId;     // 주식 ID
    private LocalDate date;    // 날짜
    private Float closePrice;  // 종가
}