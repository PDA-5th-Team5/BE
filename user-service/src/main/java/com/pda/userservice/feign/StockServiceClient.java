package com.pda.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @GetMapping("api/stocks/{userId}/my/comments")
    String getMyStockComments(@PathVariable("userId") String userId);

}