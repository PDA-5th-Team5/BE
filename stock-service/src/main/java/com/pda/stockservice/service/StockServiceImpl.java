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
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
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
    private final StockIndicatorThresholdsRepository stockIndicatorThresholdsRepository;
    private final StockMapper stockMapper;
    private final RedisService redisService;
    private final UserServiceClient userServiceClient;

    private final Environment environment;
    private final EurekaDiscoveryClient discoveryClient;

    @Override
    @Transactional
    public List<StockResponseDTO> searchStockInfos(String market, List<String> sector, StockFilter filters, int page, String token) {
        List<Market> markets = new ArrayList<>();
        if (market.equals("ALL")) {
            markets.add(Market.KOSPI);
            markets.add(Market.KOSDAQ);
        } else {
            markets.add(Market.valueOf(market));
        }
        List<Short> favStocks = new ArrayList<>();
        if (token!=null){
            JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
            String userId = jwtUtil.getBearerUserId(token);
            favStocks = favoriteStockRepository.findStockIdsByUserId(userId);
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

            if (favStocks.contains(stock.getStockId())){
                stock.setFav(true);
            }
            else{
                stock.setFav(false);
            }

            if (snowflakeMap.containsKey(stock.getStockId())) {
                stock.setSnowflakeS(SnowflakeDTO.filterSnowflake(snowflakeMap.get(stock.getStockId()), filters));
            }

            //1주, 1년 수익률
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

            filteredStockResponses.add(StockResponseDTO.filterStockResponse(stock, filters));
        }

        return filteredStockResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public MyStockCommentsResponseDTO getCommentsByUserId(String userId) {
        List<StockComment> comments = stockCommentRepository.findByUserId(userId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.MY_COMMENTS_NOT_FOUND));
        return MyStockCommentsResponseDTO.toDTO(comments);
    }

    @Override
    public List<MyStockWatchlistResponseDTO> getMyWatchlistByUserId(String userId) {
        // 1. 사용자의 관심 주식(FavoriteStock) 목록 가져오기 (JPA 조회)
        List<FavoriteStock> favoriteStocks = favoriteStockRepository.findByUserId(userId);
        if (favoriteStocks.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 관심 주식의 Stock 엔티티 목록 추출
        List<Stock> stocks = favoriteStocks.stream()
                .map(FavoriteStock::getStock)
                .collect(Collectors.toList());

        // 3. 관심 주식 ID 리스트 추출
        List<Short> stockIds = stocks.stream()
                .map(Stock::getStockId)
                .collect(Collectors.toList());

        // 4. StockStat 엔티티에서 snowflakeS 데이터 가져오기
        List<StockStat> stockStats = stockStatRepository.findByStockIdIn(stockIds);
        Map<Short, StockStat> stockStatMap = stockStats.stream()
                .collect(Collectors.toMap(StockStat::getStockId, stockStat -> stockStat));

        // 5. Redis에서 수익률 정보 가져오기
        List<String> stockIdStrings = stockIds.stream().map(String::valueOf).collect(Collectors.toList());
        Map<String, Map<Object, Object>> stockReturns = redisService.getStockReturnsByIds(stockIdStrings);

        // 5-2. Redis에서 현재가 정보 가져오기
        List<String> tickers = stocks.stream().map(Stock::getTicker).toList();
        Map<String, Map<Object, Object>> stockPrices = redisService.getStockCurrentPricesByTickers(tickers);

        // 6. 데이터 매핑 후 응답 객체 생성
        List<MyStockWatchlistResponseDTO> watchlistResponses = new ArrayList<>();

        for (Stock stock : stocks) {
            String stockId = String.valueOf(stock.getStockId());
            String ticker = stock.getTicker();

            // 7. StockStat에서 snowflakeS 데이터 가져오기 (기본값 null 설정)
            StockStat stockStat = stockStatMap.get(stock.getStockId());
            FixedStockSnowflakeResponseDTO fixedStockSnowflakeResponseDTO = FixedStockSnowflakeResponseDTO.builder()
                    .per(stockStat != null ? stockStat.getPer() : null)
                    .lbltRate(stockStat != null ? stockStat.getLbltRate() : null)
                    .marketCap(stockStat != null ? stockStat.getMarketCap() : null)
                    .divYield(stockStat != null ? stockStat.getDividendYield() : null)
                    .foreignerRatio(stockStat != null ? stockStat.getForeignerRatio() : null)
                    .build();


            // 8. 기본 주식 정보 설정
            MyStockWatchlistResponseDTO responseDTO = MyStockWatchlistResponseDTO.builder()
                    .snowflakeS(fixedStockSnowflakeResponseDTO) // snowflakeS 데이터 추가
                    .stockId(stock.getStockId())
                    .ticker(stock.getTicker())
                    .marketType(stock.getMarketType().name())
                    .companyName(stock.getCompanyName())
                    .sector(stock.getSector())
                    .companyOverview(stock.getCompanyOverview())
                    .marketCap(stock.getMarketCap())
                    .bsopPrti(stock.getBsopPrti())
                    .per(stock.getPer())
                    .bps(stock.getBps())
                    .build();

            // 9. Redis에서 수익률 정보 매핑
            Map<Object, Object> periodChangeRate = stockReturns.get(stockId);
            if (periodChangeRate != null) {
                if (periodChangeRate.containsKey("week_rate_change")) {
                    responseDTO.setWeekRateChange(Double.parseDouble(periodChangeRate.get("week_rate_change").toString()));
                }
                if (periodChangeRate.containsKey("year_rate_change")) {
                    responseDTO.setYearRateChange(Double.parseDouble(periodChangeRate.get("year_rate_change").toString()));
                }
            }

            // 현재가 및 변동률 데이터 반영
            Map<Object, Object> priceData = stockPrices.get(ticker);

            if (priceData != null) {
                if (priceData.containsKey("currentPrice")) {
                    responseDTO.setCurrentPrice((int) Double.parseDouble(priceData.get("currentPrice").toString()));
                }
                if (priceData.containsKey("changeRate")) {
                    responseDTO.setChangeRate(Double.parseDouble(priceData.get("changeRate").toString()));
                }
            }

            watchlistResponses.add(responseDTO);
        }

        return watchlistResponses;
    }

    @Override
    public PortfolioSummaryResponseDTO getStocksSummary(String marketType, List<String> sector, StockFilter filters, String token) {
        List<Market> markets = new ArrayList<>();
        if (marketType.equals("ALL")) {
            markets.add(Market.KOSPI);
            markets.add(Market.KOSDAQ);
        } else {
            markets.add(Market.valueOf(marketType));
        }

        List<SnowflakeDTO> stockStats = stockMapper.searchStockStatIds(markets, sector, filters, 0, 24);
        if (stockStats.isEmpty()) {
            return null;
        }

        List<Integer> stockIds = stockStats.stream()
                .map(SnowflakeDTO::getStockId)
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        List<StockResponseDTO> stocks = stockMapper.findStocksByIds(stockIds);

        // 평균값을 계산할 변수 초기화
        double totalMarketCap = 0;
        double totalPer = 0;
        double totalDividendYield = 0;
        double totalLbltRate = 0;
        int countMarketCap = 0;
        int countPer = 0;
        int countDividendYield = 0;
        int countLbltRate = 0;

        for (StockResponseDTO stock : stocks) {
            if (stock.getMarketCap() != null) {
                totalMarketCap += stock.getMarketCap();
                countMarketCap++;
            }
            if (stock.getPer() != null) {
                totalPer += stock.getPer();
                countPer++;
            }
            if (stock.getDividendYield() != null) {
                totalDividendYield += stock.getDividendYield();
                countDividendYield++;
            }
            if (stock.getLbltRate() != null) {
                totalLbltRate += stock.getLbltRate();
                countLbltRate++;
            }
        }

        // 평균 계산 (해당 값이 존재하는 경우에만 계산)
        int avgMarketCap = countMarketCap > 0 ? (int) (totalMarketCap / countMarketCap) : 0;
        double avgPer = countPer > 0 ? Math.round((totalPer / countPer) * 100.0) / 100.0 : 0;
        double avgDividend = countDividendYield > 0 ? Math.round((totalDividendYield / countDividendYield) * 100.0) / 100.0 : 0;
        double avgDebt = countLbltRate > 0 ? Math.round((totalLbltRate / countLbltRate) * 100.0) / 100.0 : 0;

        return new PortfolioSummaryResponseDTO(avgMarketCap, avgPer, avgDebt, avgDividend);
    }


    // 개별 종목 정보 조회
    @Override
    @Transactional(readOnly = true)
    public StockInfoResponseDTO getStocks(Short stockId, String token){
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));
        StockStat stockStat = stockStatRepository.findById(stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));
        StockInfoResponseDTO responseDTO = StockInfoResponseDTO.toDTO(stock, stockStat);

        String ticker = stock.getTicker();
        List<String> tickerList = Collections.singletonList(ticker);
        Map<String, Map<Object, Object>> stockPrices = redisService.getStockCurrentPricesByTickers(tickerList);

        Map<Object, Object> priceData = stockPrices.get(ticker);
        if (priceData != null){
            //현재가
            if (priceData.containsKey("changeRate")) {
            int currentPrice = (int) Double.parseDouble(priceData.get("currentPrice").toString());
            responseDTO.getStockInfo().setCurrentPrice(currentPrice);
        }
            //변동률
            if (priceData.containsKey("changeRate")){
                Double changeRate = Double.parseDouble(priceData.get("changeRate").toString());
                responseDTO.getStockInfo().setChangeRate(changeRate);
                }
        }

        List<String> stockIdList = Collections.singletonList(stockId.toString());
        Map<String, Map<Object, Object>> periodChangeRate = redisService.getStockReturnsByIds(stockIdList);
        if (periodChangeRate != null){
            Map<Object, Object> stockRateData = periodChangeRate.get(stockId.toString());
            if (stockRateData != null) {
                //1주
                if (stockRateData.containsKey("week_rate_change")){
                    Double weekRate = Double.parseDouble(stockRateData.get("week_rate_change").toString());
                    responseDTO.getStockInfo().setWeekRateChange(weekRate);
                }
                //1년
                if (stockRateData.containsKey("year_rate_change")){
                    Double yearRate = Double.parseDouble(stockRateData.get("year_rate_change").toString());
                    responseDTO.getStockInfo().setYearRateChange(yearRate);
                }
            }
        }

        responseDTO.getStockInfo().setFav(false);

        // 토큰이 있는 경우만 북마크 확인
        if (token != null) {
            try {
                JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
                String userId = jwtUtil.getBearerUserId(token);

                boolean exists = false;

                exists = favoriteStockRepository.existsByUserIdAndStock_StockId(userId, stockId);

                responseDTO.getStockInfo().setFav(exists);
            } catch (Exception e) {
                // 오류 발생 시 기본값 false 유지
            }
        }



        return responseDTO;
    }

    //캔들 차트 데이터 조회
    @Override
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

        String targetSector = CompetitorsResponseDTO.determineSector(stockId, sector, stockRepository);

        List<Stock> topStocks = stockRepository.findTopCompetitors(targetSector);

        List<Short> orderedStockIds = topStocks.stream()
                .filter(stock -> !stock.getStockId().equals(stockId))
                .map(Stock::getStockId)
                .collect(Collectors.toList());

        List<StockStat> stockStats = stockStatRepository.findByStockIdIn(orderedStockIds);

        return CompetitorsResponseDTO.toDTO(stockStats, orderedStockIds);
    }

    // 관심종목추가
    @Override
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
    @Override
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
    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO getComments(Short stockId) {
        if (!stockRepository.existsById(stockId)){
            throw new StockHandler(ErrorStatus.STOCK_NOT_FOUND);
        }

        List<StockComment> comments = stockCommentRepository.findCommentsByStockId(stockId);


        return CommentResponseDTO.toDTO(comments, userServiceClient);
    }

    // 댓글 작성
    @Override
    @Transactional
    public void addComments(Short stockId, String content, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

        String userId = jwtUtil.getBearerUserId(token);

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.STOCK_NOT_FOUND));

        StockComment comment = StockComment.builder()
                .content(content)
                .stock(stock)
                .userId(userId)
                .build();

        stockCommentRepository.save(comment);
    }

    //댓글삭제
    @Override
    @Transactional
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

    //댓글수정
    @Override
    @Transactional
    public void updateComments(Long commentId, String content, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

        String userId = jwtUtil.getBearerUserId(token);

        StockComment stockComment = stockCommentRepository.findById(commentId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.COMMENT_NOT_FOUND));

        if (!stockComment.getUserId().equals(userId)) {
            throw new StockHandler(ErrorStatus.NOT_AUTHORIZED);
        }

        stockComment.updateContent(content);
        stockCommentRepository.save(stockComment);
    }

    // 주식 검색 자동완성
    @Override
    @Transactional(readOnly = true)
    public List<StockAutoCompleteResponseDTO> searchStocks(String keyword) {
        System.out.println(keyword);
        List<Stock> stocks = stockRepository.findByTickerContainingOrCompanyNameContaining(keyword,keyword);
        System.out.println(stocks.get(0).toString());

        return stocks.stream()
                .map(StockAutoCompleteResponseDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSectors() {
        return stockRepository.findDistinctSectors();
    }

    @Override
    public ThresholdsResponseDTO getAllStockIndicatorThresholds() {
        List<StockIndicatorThresholds> thresholds = stockIndicatorThresholdsRepository.findAll();

        ThresholdsResponseDTO responseDTO = new ThresholdsResponseDTO();
        responseDTO.setPbr(getValues(thresholds, "pbr"));
        responseDTO.setNtinInrt(getValues(thresholds, "ntin_inrt"));
        responseDTO.setBps(getValues(thresholds, "bps"));
        responseDTO.setRoeVal(getValues(thresholds, "roe_val"));
        responseDTO.setCrntRate(getValues(thresholds, "crnt_rate"));
        responseDTO.setSaleAccount(getValues(thresholds, "sale_account"));
        responseDTO.setGrs(getValues(thresholds, "grs"));
        responseDTO.setEps(getValues(thresholds, "eps"));
        responseDTO.setBsopPrfiInrt(getValues(thresholds, "bsop_prfi_inrt"));
        responseDTO.setMarketCap(getValues(thresholds, "market_cap"));
        responseDTO.setLbltRate(getValues(thresholds, "lblt_rate"));
        responseDTO.setSps(getValues(thresholds, "sps"));
        responseDTO.setForeignerRatio(getValues(thresholds, "foreigner_ratio"));
        responseDTO.setDividendYield(getValues(thresholds, "dividend_yield"));
        responseDTO.setPer(getValues(thresholds, "per"));
        responseDTO.setThtrNtin(getValues(thresholds, "thtr_ntin"));
        responseDTO.setBsopPrti(getValues(thresholds, "bsop_prti"));

        return responseDTO;
    }

    private List<Double> getValues(List<StockIndicatorThresholds> thresholds, String indicator) {
        return thresholds.stream()
                .filter(threshold -> threshold.getIndicator().equals(indicator))
                .map(threshold -> threshold.getMaxValue() != null ? threshold.getMaxValue() : 0.0)
                .collect(Collectors.toList());
    }
}
