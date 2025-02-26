package com.pda.stockservice.controller;

import com.pda.stockservice.dto.response.StockInfoResponseDTO;

import com.pda.stockservice.service.StockService;
import com.pda.stockservice.service.StockServiceImpl;
import com.pda.utilservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
