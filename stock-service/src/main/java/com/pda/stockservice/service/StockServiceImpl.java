package com.pda.stockservice.service;

import com.pda.stockservice.dto.response.StockInfoResponseDTO;
import com.pda.stockservice.entity.Stock;
import com.pda.stockservice.repository.StockRepository;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.StockHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    // 개별 종목 정보 조회
    @Transactional(readOnly = true)
    public StockInfoResponseDTO getStocks(Short stockId){
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));
        return StockInfoResponseDTO.toDTO(stock);
    }
}
