package com.pda.stockservice.controller;

import com.pda.stockservice.dto.request.PortfolioMarketGraphRequestDTO;
import com.pda.stockservice.dto.request.StockFilterRequest;
import com.pda.stockservice.dto.request.StockLineGraphRequestDTO;
import com.pda.stockservice.dto.request.StockSearchDTO;
import com.pda.stockservice.dto.response.*;

import com.pda.stockservice.enums.Market;
import com.pda.stockservice.feign.UserServiceClient;
import com.pda.stockservice.service.StockService;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ApiResponse<StockSearchResponseDTO> searchStockStatIds(
            @RequestBody StockFilterRequest request,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "0") int page) {

        StockSearchResponseDTO responseDTO = stockService.searchStockInfos(request.getMarketType(), request.getSector(), request.getFilters(), page, token);

        return ApiResponse.onSuccess(responseDTO);
    }

    //개별종목 정보조회
    @GetMapping("/{stockId}")
    public ApiResponse<StockInfoResponseDTO> getStocks(@PathVariable("stockId") Short stockId, @RequestHeader(value = "Authorization", required = false) String token) {
        StockInfoResponseDTO stockInfoResponseDTO = stockService.getStocks(stockId,token);
        return ApiResponse.onSuccess(stockInfoResponseDTO);
    }

    //관심종목 추가
    @PostMapping("/{stockId}/watchlist")
    public ApiResponse<Void> addFavoriteStock(@PathVariable("stockId") Short stockId, @RequestHeader(value = "Authorization", required = false) String token){
        stockService.addFavoriteStock(stockId, token);
        return ApiResponse.onSuccess(HttpStatus.OK.value(), "성공입니다.");

    }

    //관심종목 삭제
    @DeleteMapping("/{stockId}/watchlist")
    public  ApiResponse<SuccessStatus> deleteFavoriteStock(@PathVariable("stockId") Short stockId, @RequestHeader(value = "Authorization", required = false) String token){
        stockService.deleteFavoriteStock(stockId, token);
        return ApiResponse.onSuccess(null);
    }

    //캔들차트 데이터조회
    @GetMapping("/{stockId}/candle")
    public ApiResponse<CandleResponseDTO> getCandle(@PathVariable("stockId") Short stockId) {
        CandleResponseDTO candleResponseDTO = stockService.getCandle(stockId);
        return ApiResponse.onSuccess(candleResponseDTO);
    }

    //경쟁사 정보조회
    @GetMapping("/{stockId}/competitors")
    public ApiResponse<CompetitorsResponseDTO> getCompetitors(
            @PathVariable("stockId") Short stockId) {
        CompetitorsResponseDTO competitorsResponseDTO = stockService.getCompetitors(stockId);
        return ApiResponse.onSuccess(competitorsResponseDTO);
    }

    //댓글조회
    @GetMapping("/{stockId}/comments")
    public ApiResponse<CommentResponseDTO> getComments(
            @PathVariable("stockId") Short stockId,
            @RequestParam(value = "page", required = false) String page) {
        CommentResponseDTO commentResponseDTO = stockService.getComments(stockId);
        return ApiResponse.onSuccess(commentResponseDTO);
    }

    // 댓글 작성
    @PostMapping("/{stockId}/comments")
    public ApiResponse<Void> addComments(
            @PathVariable("stockId") Short stockId,
            @RequestBody String content, @RequestHeader(value = "Authorization", required = false) String token) {
        stockService.addComments(stockId, content, token);
        return ApiResponse.onSuccess(HttpStatus.OK.value(),"성공입니다.");
    }


    // 댓글 삭제
    @DeleteMapping("/{stockId}/comments/{commentId}")
    public ApiResponse<Void> deleteComments(
            @PathVariable("stockId") Short stockId,
            @PathVariable("commentId") Long commentId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        stockService.deleteComments(commentId, token);
        return ApiResponse.onSuccess(HttpStatus.OK.value(), "성공입니다.");
    }

    //댓글 수정
    @PatchMapping("/{stockId}/comments/{commentId}")
    public ApiResponse<Void> updateComments(
            @PathVariable("stockId") Short stockId,
            @PathVariable("commentId") Long commentId,
            @RequestBody String content,
            @RequestHeader(value = "Authorization", required = false) String token) {
        stockService.updateComments(commentId, content, token);
        return ApiResponse.onSuccess(HttpStatus.OK.value(), "성공입니다.");
    }

    // userId로 종목 댓글 조회
    @GetMapping("/{userId}/my/comments")
    public MyStockCommentsResponseDTO getNickname(@PathVariable String userId) {
        return stockService.getCommentsByUserId(userId);
    }


    // openfeign 테스트
    @GetMapping("/openfeign")
    public String test() {

        System.out.println("StockController.test");

        System.out.println(userServiceClient.getNickname("42b57999-e5ac-4869-a090-ca247852ba6c"));

        return "test";

    }

    // userId로 관심 종목 조회
    @GetMapping("/{userId}/my/watchlist")
    public List<MyStockWatchlistResponseDTO> getMyWatchlist(@PathVariable String userId) {
        return stockService.getMyWatchlistByUserId(userId);
    }

    @PostMapping("/search")
    public ApiResponse<List<StockAutoCompleteResponseDTO>> searchStocks(@RequestBody StockSearchDTO keyword) {
        List<StockAutoCompleteResponseDTO> response = stockService.searchStocks(keyword.getKeyword());
        return ApiResponse.onSuccess(response);
    }

    // 포트폴리오 filter값으로 해당 포트폴리오의 평균(4가지)값 조회
    @PostMapping("/summary")
    public PortfolioSummaryResponseDTO searchStockStatIds(
            @RequestBody StockFilterRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return stockService.getStocksSummary(request.getMarketType(), request.getSector(), request.getFilters(), token);
    }

    // sector 전체 조회
    @GetMapping("/sectors")
    public ApiResponse<List<String>> getSectors() {
        List<String> response = stockService.getSectors();
        return ApiResponse.onSuccess(response);
    }

    // 임계값 전체 조회
    @GetMapping("/thresholds")
    public ApiResponse<ThresholdsResponseDTO> getAllStockIndicators() {
        ThresholdsResponseDTO response = stockService.getAllStockIndicatorThresholds();
        return ApiResponse.onSuccess(response);
    }

    // 개별 종목 라인그래프 조회
    @GetMapping("/graph")
    public ApiResponse<StockLineGraphResponseDTO> getStockLineGraph(@RequestBody StockLineGraphRequestDTO request) {
        StockLineGraphResponseDTO response = stockService.getStockLineGraph(request.getStockId());
        return ApiResponse.onSuccess(response);
    }

    //나의 포트폴리오 vs 시장 그래프 조회 OpenFeign 코드
    @PostMapping("/my/graph")
    public PortfolioMarketGraphResponseDTO getMyPortfolioMarketGraph(@RequestBody PortfolioMarketGraphRequestDTO request, @RequestParam(value = "market") Market market) {
        return stockService.getMyPortfolioMarketGraph(request, market);
    }

    // 공유 포트폴리오 vs 시장 그래프 조회 OpenFeign 코드
    @PostMapping("/share/graph")
    public PortfolioMarketGraphResponseDTO getSharePortfolioMarketGraph(@RequestBody PortfolioMarketGraphRequestDTO request, @RequestParam(value = "market") Market market) {
        return stockService.getMyPortfolioMarketGraph(request, market);
    }
}
