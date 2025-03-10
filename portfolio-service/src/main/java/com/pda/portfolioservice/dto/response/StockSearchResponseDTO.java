package com.pda.portfolioservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StockSearchResponseDTO {
    private List<StockResponseDTO> stocks;
    private long totalCount;
}