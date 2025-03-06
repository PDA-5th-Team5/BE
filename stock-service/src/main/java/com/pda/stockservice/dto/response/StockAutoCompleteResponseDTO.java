package com.pda.stockservice.dto.response;

import com.pda.stockservice.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockAutoCompleteResponseDTO {

    private Short id;
    private String ticker;
    private String companyName;

    public static StockAutoCompleteResponseDTO toDTO(Stock stock) {
        return new StockAutoCompleteResponseDTO(
                stock.getStockId(),
                stock.getTicker(),
                stock.getCompanyName()
        );
    }

}
