package com.pda.stockservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class  CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 기본 캐시 설정 (TTL 없음)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 특정 캐시에 대한 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("portfolioAverageClosePrice",
                defaultConfig.entryTtl(Duration.ofDays(1))); // 1일 TTL

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig) // 기본 캐시 설정 (TTL 없음)
                .withInitialCacheConfigurations(cacheConfigurations) // 특정 캐시 TTL 적용
                .build();
    }
}