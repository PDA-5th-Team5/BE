package com.pda.portfolioservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @GetMapping("/markets")
    public List<String> getMarkets(@RequestParam(required = false) String market);

    @GetMapping("/sectors")
    public List<String> getSectors(@RequestParam(required = false) String sectors);

}
