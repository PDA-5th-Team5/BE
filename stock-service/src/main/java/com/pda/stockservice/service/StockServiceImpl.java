package com.pda.stockservice.service;

import com.pda.stockservice.dto.request.SnowflakeDTO;
import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.response.*;
import com.pda.stockservice.entity.*;
import com.pda.stockservice.enums.Market;
import com.pda.stockservice.mapper.StockMapper;
import com.pda.stockservice.repository.*;
import com.pda.stockservice.feign.UserServiceClient;

import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.StockHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final FavoriteStockRepository favoriteStockRepository;
    private final StockPriceDayRepository stockPriceDayRepository;
    private final StockStatRepository stockStatRepository;
    private final StockCommentRepository stockCommentRepository;
    private final StockMapper stockMapper;
    private final RedisService redisService;
    private final UserServiceClient userServiceClient;

    private final Environment environment;
    @Override
    @Transactional
    public List<StockResponseDTO> searchStockInfos(String market, List<String> sector, StockFilter filters, int page) {
        List<Market> markets = new ArrayList<>();
        if (market.equals("ALL")) {
            markets.add(Market.KOSPI);
            markets.add(Market.KOSDAQ);
        } else {
            markets.add(Market.valueOf(market));
        }

        int limit = 24;  // 한 페이지에 24개씩
        int offset = page * limit;  // 페이지 인덱스 기반 오프셋 계산

        List<SnowflakeDTO> stockStats = stockMapper.searchStockStatIds(markets, sector, filters, offset, limit);
        if (stockStats.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> stockIds = stockStats.stream()
                .map(SnowflakeDTO::getStockId)
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        Map<Short, SnowflakeDTO> snowflakeMap = stockStats.stream()
                .collect(Collectors.toMap(SnowflakeDTO::getStockId, snowflake -> snowflake));

        List<StockResponseDTO> stocks = stockMapper.findStocksByIds(stockIds);

        List<String> stockIdStrings = stocks.stream().map(s -> String.valueOf(s.getStockId())).toList();
        Map<String, Map<Object, Object>> stockReturns = redisService.getStockReturnsByIds(stockIdStrings);
        List<String> tickers = stocks.stream().map(StockResponseDTO::getTicker).toList();
        Map<String, Map<Object, Object>> stockPrices = redisService.getStockCurrentPricesByTickers(tickers);

        List<StockResponseDTO> filteredStockResponses = new ArrayList<>();

        for (StockResponseDTO stock : stocks) {
            String stockId = String.valueOf(stock.getStockId());
            String ticker = stock.getTicker();

            if (snowflakeMap.containsKey(stock.getStockId())) {
                stock.setSnowflakeS(SnowflakeDTO.filterSnowflake(snowflakeMap.get(stock.getStockId()), filters));
            }

            Map<Object, Object> periodChangeRate = stockReturns.get(stockId);
            if (periodChangeRate != null) {
                if (periodChangeRate.containsKey("week_rate_change")) {
                    stock.setWeekRateChange(Double.parseDouble(periodChangeRate.get("week_rate_change").toString()));
                }
                if (periodChangeRate.containsKey("year_rate_change")) {
                    stock.setYearRateChange(Double.parseDouble(periodChangeRate.get("year_rate_change").toString()));
                }
            }

            filteredStockResponses.add(StockResponseDTO.filterStockResponse(stock, filters));
        }

        return filteredStockResponses;
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
    @Transactional(readOnly = true)
    public CompetitorsResponseDTO getCompetitors(Short stockId, String sector) {
        // 1. 섹터 정보 결정
        String targetSector = CompetitorsResponseDTO.determineSector(stockId, sector, stockRepository);

        // 2. 해당 섹터의 시총 상위 5개 종목 가져오기
        List<Stock> topStocks = stockRepository.findTopCompetitors(targetSector);

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
    public void deleteFavoriteStock(Short stockId, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

        String userId = jwtUtil.getBearerUserId(token);
        // 해당 사용자의 해당 종목 관심종목 찾기
        FavoriteStock favoriteStock = favoriteStockRepository.findByUserIdAndStock_StockId(userId, stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.FAVORITE_STOCK_NOT_FOUND));

        favoriteStockRepository.delete(favoriteStock);
    }

    //댓글조회
    @Transactional(readOnly = true)
    @Override
    public CommentResponseDTO getComments(Short stockId) {
        if (!stockRepository.existsById(stockId)){
            throw new StockHandler(ErrorStatus.STOCK_NOT_FOUND);
        }

        List<StockComment> comments = stockCommentRepository.findByStock_StockIdOrderByCreatedAtDesc(stockId);


        return CommentResponseDTO.toDTO(comments, userServiceClient);
    }

    // 댓글 작성
    @Transactional
    @Override
    public void addComments(Short stockId, String content, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

        String userId = jwtUtil.getBearerUserId(token);

        // 해당 주식이 존재하는지 확인
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));

        // 댓글 생성
        StockComment comment = StockComment.builder()
                .content(content)
                .stock(stock)
                .userId(userId)
                .build();

        // 댓글 저장
        stockCommentRepository.save(comment);
    }

    //댓글삭제
    @Transactional
    @Override
    public void deleteComments(Long commentId, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

        String userId = jwtUtil.getBearerUserId(token);

        StockComment stockComment = stockCommentRepository.findById(commentId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.COMMENT_NOT_FOUND));

        if (!stockComment.getUserId().equals(userId)) {
            throw new StockHandler(ErrorStatus.NOT_AUTHORIZED);
        }

        stockCommentRepository.delete(stockComment);
    }

}
