package com.pda.userservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "refreshToken", timeToLive = 1209600) // 예시: 2주(14일) TTL 설정
public class Refresh {

    @Id
    private String username;   // 사용자명을 Key로 사용
    private String refresh;    // Refresh 토큰
}

