package com.pda.stockservice.service;

import com.pda.stockservice.dto.response.CandleResponseDTO;
import com.pda.stockservice.dto.response.StockInfoResponseDTO;
import com.pda.stockservice.entity.FavoriteStock;
import com.pda.stockservice.entity.Stock;
import com.pda.stockservice.entity.StockPriceDay;
import com.pda.stockservice.repository.FavoriteStockRepository;
import com.pda.stockservice.repository.StockPriceDayRepository;
import com.pda.stockservice.repository.StockRepository;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.StockHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final FavoriteStockRepository favoriteStockRepository;
    private final StockPriceDayRepository stockPriceDayRepository;
    // 개별 종목 정보 조회
    @Transactional(readOnly = true)
    public StockInfoResponseDTO getStocks(Short stockId){
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));
        return StockInfoResponseDTO.toDTO(stock);
    }

    //캔들 차트 데이터 조회
    @Transactional(readOnly = true)
    public CandleResponseDTO getCandle(Short stockId) {
        //주식이 존재하는 지 확인
        if (!stockRepository.existsById(stockId)){
            throw new StockHandler(ErrorStatus.STOCK_NOT_FOUND);
        }

        List<StockPriceDay> stockPriceDays = stockPriceDayRepository.findCandleDateByStockId(stockId);

        return CandleResponseDTO.toDTO(stockPriceDays);

    }

    // 관심종목추가
    @Transactional
    public void addFavoriteStock(Short stockId) {
        //userId 하드코딩
        String userId = "1";
        // 이미 추가된 관심종목인지 확인
        if (favoriteStockRepository.existsByUserIdAndStock_StockId(userId, stockId)) {
            return;
        }
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(()-> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));

        FavoriteStock favoriteStock = FavoriteStock.builder()
                .stock(stock)
                .userId(userId)
                .build();

        favoriteStockRepository.save(favoriteStock);


    }

    //관심종목 삭제
    @Transactional
    public void deleteFavoriteStock(Short stockId) {
        String userId = "1";
        // 해당 사용자의 해당 종목 관심종목 찾기
        FavoriteStock favoriteStock = favoriteStockRepository.findByUserIdAndStock_StockId(userId, stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.FAVORITE_STOCK_NOT_FOUND));

        favoriteStockRepository.delete(favoriteStock);
    }

}
