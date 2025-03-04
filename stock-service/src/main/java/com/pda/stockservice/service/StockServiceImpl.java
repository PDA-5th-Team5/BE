package com.pda.stockservice.service;

import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.response.*;
//import com.pda.stockservice.dto.response.CommentResponseDTO;
import com.pda.stockservice.entity.*;
import com.pda.stockservice.enums.Market;
import com.pda.stockservice.mapper.StockMapper;
import com.pda.stockservice.repository.*;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.StockHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final FavoriteStockRepository favoriteStockRepository;
    private final StockPriceDayRepository stockPriceDayRepository;
    private final StockStatRepository stockStatRepository;
    private final StockMapper stockMapper;
    private final RedisService redisService;
    private final StockCommentRepository stockCommentRepository;
    private final Environment environment;
    @Override
    public List<StockResponseDTO> searchStockInfos(String market, List<String> sector, StockFilter filters) {
        List<Market> markets = new ArrayList<>();
        if (market.equals("ALL")){
            markets.add(Market.KOSPI);
            markets.add(Market.KOSDAQ);
        }
        else {
            markets.add(Market.valueOf(market)); // 문자열을 enum으로 변환하여 추가
        }

        List<Integer> stockIds = stockMapper.searchStockStatIds(markets, sector, filters);
        if (stockIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<StockResponseDTO> stocks = stockMapper.findStocksByIds(stockIds);

        // stock_id 리스트 가져오기
        List<String> stockIdStrings = stocks.stream()
                .map(stock -> String.valueOf(stock.getStockId()))
                .collect(Collectors.toList());

        // 한 번에 Redis에서 수익률 데이터 가져오기
        Map<String, Map<Object, Object>> stockReturns = redisService.getStockReturnsByIds(stockIdStrings);

        // 한 번에 Redis에서 현재가 및 변동률 데이터 가져오기 (티커 기준 조회)
        List<String> tickers = stocks.stream()
                .map(StockResponseDTO::getTicker)  // 티커 리스트 추출
                .collect(Collectors.toList());

        Map<String, Map<Object, Object>> stockPrices = redisService.getStockCurrentPricesByTickers(tickers);

        for (StockResponseDTO stock : stocks) {
            String stockId = String.valueOf(stock.getStockId());
            String ticker = stock.getTicker();

            // 주식 수익률 데이터 반영
            Map<Object, Object> periodChangeRate = stockReturns.get(stockId);
            if (periodChangeRate != null) {
                if (periodChangeRate.containsKey("week_rate_change")) {
                    stock.setWeekRateChange(Double.parseDouble(periodChangeRate.get("week_rate_change").toString()));
                }
                if (periodChangeRate.containsKey("year_rate_change")) {
                    stock.setYearRateChange(Double.parseDouble(periodChangeRate.get("year_rate_change").toString()));
                }
            }

            // 현재가 및 변동률 데이터 반영
            Map<Object, Object> priceData = stockPrices.get(ticker);

            if (priceData != null) {
                if (priceData.containsKey("currentPrice")) {
                    stock.setCurrentPrice((int) Double.parseDouble(priceData.get("currentPrice").toString()));
                }
                if (priceData.containsKey("changeRate")) {
                    stock.setChangeRate(Double.parseDouble(priceData.get("changeRate").toString()));
                }
            }
        }

        return stocks;
    }

    @Override
    @Transactional(readOnly = true)
    public MyCommentsResponseDTO getCommentsByUserId(String userId) {
        List<StockComment> comments = stockCommentRepository.findByUserId(userId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.MY_COMMENTS_NOT_FOUND));
        return MyCommentsResponseDTO.toDTO(comments);
    }


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

    //개별종목 경쟁사 조회
    @Override
    public CompetitorsResponseDTO getCompetitors(Short stockId, String sector) {
        // 1. 섹터 정보 결정
        String targetSector = sector;
        if (targetSector == null || targetSector.isEmpty()) {
            Stock stock = stockRepository.findById(stockId)
                    .orElseThrow(() -> new EntityNotFoundException("Stock not found"));
            targetSector = stock.getSector();
        }

        // 2. 해당 섹터의 시총 상위 5개 종목 가져오기
        List<Stock> topStocks = stockRepository.findTop6BySectorOrderByMarketCapDesc(targetSector);

        // 3. 종목 ID 리스트 추출
        List<Short> orderedStockIds = topStocks.stream()
                .filter(stock -> !stock.getStockId().equals(stockId))
                .map(Stock::getStockId)
                .collect(Collectors.toList());

        // 4. 해당 종목들의 StockStat 정보 가져오기
        List<StockStat> stockStats = stockStatRepository.findByStockIdIn(orderedStockIds);

        // 5. DTO로 변환하여 반환 (변환 로직은 DTO 클래스에서)
        return CompetitorsResponseDTO.toDTO(stockStats, orderedStockIds);
    }

    // 관심종목추가
    @Transactional
    public void addFavoriteStock(Short stockId, String token) {

        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

        String userId = jwtUtil.getBearerUserId(token);

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

    //댓글조회
//    @Transactional(readOnly = true)
//    @Override
//    public CommentResponseDTO getComments(Short stockId) {
//        // 해당 주식이 존재하는지 확인
//        if (!stockRepository.existsById(stockId)) {
//            throw new StockHandler(ErrorStatus.STOCK_NOT_FOUND);
//        }
//
//        // 해당 주식에 대한 댓글 목록 조회
//        List<StockComment> comments = StockCommentRepository.findByStock_StockIdOrderByCreatedAtDesc(stockId);
//
//        // 한 번에 DTO로 변환
//        return CommenResponseDTO.from(comments, userServiceClient);
//    }


}
