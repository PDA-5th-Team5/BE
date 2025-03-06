package com.pda.portfolioservice.feign;

import com.pda.portfolioservice.dto.request.StockFilterRequest;
import com.pda.portfolioservice.dto.response.PortfolioSummaryResponseDTO;
import com.pda.portfolioservice.dto.response.StockResponseDTO;
import com.pda.utilservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @GetMapping("/markets")
    public List<String> getMarkets(@RequestParam(required = false) String market);

    @GetMapping("/sectors")
    public List<String> getSectors(@RequestParam(required = false) String sectors);

    @PostMapping("/api/stocks/filter")
    public ApiResponse<List<StockResponseDTO>> searchStockStatIds(
            @RequestBody StockFilterRequest request,
            @RequestParam(defaultValue = "0") int page);

    @PostMapping("/api/stocks/summary")
    public PortfolioSummaryResponseDTO getStocksSummary(@RequestBody StockFilterRequest request);
}
