package com.pda.stockservice.service;

import com.pda.stockservice.dto.response.StockInfoResponseDTO;

public interface StockService {
    StockInfoResponseDTO getStocks(Short stockId);
    //로그인기능구현 후 useId추가 예정
    void addFavoriteStock(Short stockId);
    void deleteFavoriteStock(Short stockId);
}
