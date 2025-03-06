package com.pda.stockservice.service;

import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.response.*;

import java.util.List;

public interface StockService {
    StockInfoResponseDTO getStocks(Short stockId);
    CandleResponseDTO getCandle(Short stockId);

    //개별종목 경쟁사 조회
    CompetitorsResponseDTO getCompetitors(Short stockId, String sector);
    //댓글
    CommentResponseDTO getComments(Short stockId);
    void addComments(Short stockId, String content, String token);
    void deleteComments(Long commentId, String token);
    void updateComments(Long commentId, String content, String token);

    void addFavoriteStock(Short stockId, String token);
    void deleteFavoriteStock(Short stockId, String token);

    List<StockResponseDTO> searchStockInfos(String market, List<String> sector, StockFilter filters, int page, String token);

    MyStockCommentsResponseDTO getCommentsByUserId(String userId);

    List<MyStockWatchlistResponseDTO> getMyWatchlistByUserId(String userId);
}
