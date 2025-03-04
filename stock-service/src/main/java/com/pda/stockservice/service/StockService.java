package com.pda.stockservice.service;

import com.pda.stockservice.dto.response.CandleResponseDTO;
import com.pda.stockservice.dto.response.CompetitorsResponseDTO;
import com.pda.stockservice.dto.response.StockInfoResponseDTO;

public interface StockService {
    StockInfoResponseDTO getStocks(Short stockId);
    CandleResponseDTO getCandle(Short stockId);

    //개별종목 경쟁사 조회
    CompetitorsResponseDTO getCompetitors(Short stockId, String sector);

    //로그인기능구현 후 useId추가 예정
    void addFavoriteStock(Short stockId);
    void deleteFavoriteStock(Short stockId);
}
