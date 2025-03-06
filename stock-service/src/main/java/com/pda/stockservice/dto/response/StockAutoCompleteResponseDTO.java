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

    private Short stockId;
    private String companyName;
    private String ticker;

    public static StockAutoCompleteResponseDTO toDTO (Stock stock) {
        return StockAutoCompleteResponseDTO.builder().build()
    }

}
