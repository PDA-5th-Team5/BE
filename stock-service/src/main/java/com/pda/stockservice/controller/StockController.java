package com.pda.stockservice.controller;

import com.pda.stockservice.dto.response.StockInfoResponseDTO;

import com.pda.stockservice.service.StockService;
import com.pda.stockservice.service.StockServiceImpl;
import com.pda.utilservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    //개별종목 정보조회
    @GetMapping("/{stockId}")
    public ApiResponse<StockInfoResponseDTO> getStocks(@PathVariable("stockId") Short stockId) {
        StockInfoResponseDTO stockInfoResponseDTO = stockService.getStocks(stockId);
        return ApiResponse.onSuccess(stockInfoResponseDTO);
    }

    //관심종목 추가
    @PostMapping("/{stockId}/watchlist")
    public ApiResponse<Void> addFavoriteStock(@PathVariable("stockId") Short stockId){
        stockService.addFavoriteStock(stockId);
        return ApiResponse.onSuccess(null);
    }

    //관심종목 삭제
    @DeleteMapping("/{stockId}/watchlist")
    public  ApiResponse<Void> deleteFavoriteStock(@PathVariable("stockId") Short stockId){
        stockService.deleteFavoriteStock(stockId);
        return ApiResponse.onSuccess(null);
    }
}
