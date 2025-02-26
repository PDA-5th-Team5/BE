package com.pda.stockservice.service;

import com.pda.stockservice.dto.response.StockInfoResponseDTO;

public interface StockService {
    StockInfoResponseDTO getStocks(Short stockId);

}
