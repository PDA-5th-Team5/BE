package com.pda.stockservice.controller;

import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.request.StockFilterRequest;
import com.pda.stockservice.dto.response.*;

import com.pda.stockservice.entity.Stock;
import com.pda.stockservice.feign.UserServiceClient;
import com.pda.stockservice.service.StockService;
import com.pda.stockservice.service.StockServiceImpl;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.SuccessStatus;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;
    private final UserServiceClient userServiceClient;

    @GetMapping("/test")
    public String test2() {
        return "stock test";
    }
    // 특정 조건으로 주식 종목 검색
    @PostMapping("/filter")
    public ApiResponse<List<StockResponseDTO>> searchStockStatIds(
            @RequestBody StockFilterRequest request) {
        List<StockResponseDTO> stocks = stockService.searchStockInfos(request.getMarketType(), request.getSector(), request.getFilters());
        return ApiResponse.onSuccess(stocks);
    }

    //개별종목 정보조회
    @GetMapping("/{stockId}")
    public ApiResponse<StockInfoResponseDTO> getStocks(@PathVariable("stockId") Short stockId) {
        StockInfoResponseDTO stockInfoResponseDTO = stockService.getStocks(stockId);
        return ApiResponse.onSuccess(stockInfoResponseDTO);
    }

    //관심종목 추가
    @PostMapping("/{stockId}/watchlist")
    public ApiResponse<SuccessStatus> addFavoriteStock(@PathVariable("stockId") Short stockId, @RequestHeader(value = "Authorization", required = false) String token){
        stockService.addFavoriteStock(stockId, token);
        return ApiResponse.onSuccess(SuccessStatus.OK);
    }

    //관심종목 삭제
    @DeleteMapping("/{stockId}/watchlist")
    public  ApiResponse<SuccessStatus> deleteFavoriteStock(@PathVariable("stockId") Short stockId){
        stockService.deleteFavoriteStock(stockId);
        return ApiResponse.onSuccess(null);
    }
    //캔들차트 데이터조회
    @GetMapping("/{stockId}/candle")
    public ApiResponse<CandleResponseDTO> getCandle(@PathVariable("stockId") Short stockId) {
        CandleResponseDTO candleResponseDTO = stockService.getCandle(stockId);
        return ApiResponse.onSuccess(candleResponseDTO);
    }
    //댓글
//    @GetMapping("/api/stocks/{stockId}/comments")
//    public ApiResponse<CommentResponseDTO> getComments(
//            @PathVariable
//            )

    //경쟁사 정보조회
    @GetMapping("/{stockId}/competitors")
    public ApiResponse<CompetitorsResponseDTO> getCompetitors(
            @PathVariable("stockId") Short stockId,
            @RequestParam(value = "sector", required = false) String sector) {
        CompetitorsResponseDTO competitorsResponseDTO = stockService.getCompetitors(stockId, sector);
        return ApiResponse.onSuccess(competitorsResponseDTO);
    }

    // userId로 종목 댓글 조회
    @GetMapping("/{userId}/my/comments")
    public ApiResponse<MyCommentsResponseDTO> getNickname(@PathVariable String userId) {
        MyCommentsResponseDTO commentsResponseDTO = stockService.getCommentsByUserId(userId);
        return ApiResponse.onSuccess(commentsResponseDTO);
    }


    // openfeign 테스트
    @GetMapping("/openfeign")
    public String test() {

        System.out.println("StockController.test");

        System.out.println(userServiceClient.getNickname("42b57999-e5ac-4869-a090-ca247852ba6c"));

        return "test";

    }

}
