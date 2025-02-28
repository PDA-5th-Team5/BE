package com.pda.stockservice.controller;

import com.pda.stockservice.dto.response.CandleResponseDTO;
import com.pda.stockservice.dto.response.StockInfoResponseDTO;

import com.pda.stockservice.feign.UserServiceClient;
import com.pda.stockservice.service.StockService;
import com.pda.stockservice.service.StockServiceImpl;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.SuccessStatus;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<SuccessStatus> addFavoriteStock(@PathVariable("stockId") Short stockId){
        stockService.addFavoriteStock(stockId);
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

    @GetMapping("/openfeign")
    public String test() {

        System.out.println("StockController.test");

        System.out.println(userServiceClient.getNickname("42b57999-e5ac-4869-a090-ca247852ba6c"));

        return "test";
    }

}
