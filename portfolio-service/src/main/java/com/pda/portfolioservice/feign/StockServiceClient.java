package com.pda.portfolioservice.feign;

import com.pda.portfolioservice.dto.request.PortfolioMarketGraphRequestDTO;
import com.pda.portfolioservice.dto.request.StockFilterRequest;
import com.pda.portfolioservice.dto.response.PortfolioMarketGraphResponseDTO;
import com.pda.portfolioservice.dto.response.PortfolioSummaryResponseDTO;
import com.pda.portfolioservice.dto.response.StockResponseDTO;
import com.pda.portfolioservice.enums.Market;
import com.pda.portfolioservice.dto.response.StockSearchResponseDTO;
import com.pda.utilservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @GetMapping("/markets")
    public List<String> getMarkets(@RequestParam(required = false) String market);

    @GetMapping("/sectors")
    public List<String> getSectors(@RequestParam(required = false) String sectors);

    @PostMapping("/api/stocks/filter")
    public ApiResponse<StockSearchResponseDTO> searchStockStatIds(
            @RequestBody StockFilterRequest request,
            @RequestParam(defaultValue = "0") int page);

    @PostMapping("/api/stocks/summary")
    public PortfolioSummaryResponseDTO getStocksSummary(@RequestBody StockFilterRequest request);

    @PostMapping("/api/stocks/my/graph")
    public PortfolioMarketGraphResponseDTO getMyPortfolioMarketGraph(@RequestBody PortfolioMarketGraphRequestDTO request, @RequestParam(value = "market") Market market);

    @PostMapping("/api/stocks/share/graph")
    public PortfolioMarketGraphResponseDTO getSharePortfolioMarketGraph(@RequestBody PortfolioMarketGraphRequestDTO request, @RequestParam(value = "market") Market market);
}
