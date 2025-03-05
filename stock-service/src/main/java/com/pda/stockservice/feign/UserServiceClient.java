package com.pda.stockservice.feign;

import com.pda.stockservice.dto.response.NicknameResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/{userId}/nickname")
    NicknameResponseDTO getNickname(@PathVariable("userId") String userId);
}