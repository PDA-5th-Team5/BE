package com.pda.stockservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public Map<String, Map<Object, Object>> getStockReturnsByIds(List<String> stockIds) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        return stockIds.stream()
                .collect(Collectors.toMap(
                        stockId -> stockId, // 키: stock_id
                        stockId -> hashOperations.entries(stockId), // 값: Redis에서 가져온 데이터
                        (existing, replacement) -> existing // 중복 키 발생 시 기존 값 유지
                ));
    }

    public Map<String, Map<Object, Object>> getStockCurrentPricesByTickers(List<String> tickers) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        return tickers.stream()
                .collect(Collectors.toMap(
                        ticker -> ticker, // 키: ticker
                        ticker -> hashOperations.entries(ticker), // 값: Redis에서 가져온 데이터
                        (existing, replacement) -> existing // 중복 키 발생 시 기존 값 유지
                ));
    }
}