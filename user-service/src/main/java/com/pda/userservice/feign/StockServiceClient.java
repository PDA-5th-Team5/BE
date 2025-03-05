package com.pda.userservice.feign;

import com.pda.userservice.dto.response.MyStockCommentsResponseDTO;
import com.pda.userservice.dto.response.MyStockWatchlistResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @GetMapping("api/stocks/{userId}/my/comments")
    MyStockCommentsResponseDTO getMyStockComments(@PathVariable("userId") String userId);

    @GetMapping("api/stocks/{userId}/my/watchlist")
    List<MyStockWatchlistResponseDTO> getMyStockWatchlist(@PathVariable("userId") String userId);
}