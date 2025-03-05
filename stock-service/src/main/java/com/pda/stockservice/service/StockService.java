package com.pda.stockservice.service;

import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.response.*;
import com.pda.stockservice.entity.Stock;

import java.util.List;
import java.util.Map;
import com.pda.stockservice.repository.StockCommentRepository;

public interface StockService {
    StockInfoResponseDTO getStocks(Short stockId);
    CandleResponseDTO getCandle(Short stockId);

    //개별종목 경쟁사 조회
    CompetitorsResponseDTO getCompetitors(Short stockId, String sector);
    //댓글
    CommentResponseDTO getComments(Short stockId);
    void addComments(Short stockId, String content, String token);
    void deleteComments(Long commentId, String token);


    void addFavoriteStock(Short stockId, String token);
    //로그인기능구현 후 useId추가 예정
    void deleteFavoriteStock(Short stockId);

    List<StockResponseDTO> searchStockInfos(String market, List<String> sector, StockFilter filters, int page);


    MyCommentsResponseDTO getCommentsByUserId(String userId);
}
