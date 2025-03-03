package com.pda.stockservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    // openfeign 테스트용
    @GetMapping("/user/username")
    String getNickname(@PathVariable("userId") String userId);

    @GetMapping("/user/{id}")
    String getUserNickname(@PathVariable("userId") String userId);
}