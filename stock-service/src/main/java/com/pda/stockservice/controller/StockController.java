package com.pda.stockservice.controller;

import com.pda.stockservice.dto.response.CandleResponseDTO;
import com.pda.stockservice.dto.response.CommentResponseDTO;
import com.pda.stockservice.dto.response.CompetitorsResponseDTO;
import com.pda.stockservice.dto.response.StockInfoResponseDTO;

import com.pda.stockservice.enums.Market;
import com.pda.stockservice.enums.Sectors;
import com.pda.stockservice.feign.UserServiceClient;
import com.pda.stockservice.service.StockService;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.SuccessStatus;
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





    // openfeign 테스트
    @GetMapping("/openfeign")
    public String test() {

        System.out.println("StockController.test");

        System.out.println(userServiceClient.getNickname("42b57999-e5ac-4869-a090-ca247852ba6c"));

        return "test";

    }

    // 시장 (KOSPI, KOSDAQ, ALL) 조회 OpenFeign 통신 코드
    @GetMapping("/markets")
    public List<Market> getMarkets(@RequestParam(required = false) Market market) {
        return Market.getMarkets(market);
    }

    // 섹터 조회 OpenFeign 통신 코드
    @GetMapping("/sectors")
    public List<Sectors> getSectors(@RequestParam(required = false) String sectors) {
        return Sectors.fromString(sectors);
    }

}
