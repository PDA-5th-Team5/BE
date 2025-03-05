package com.pda.userservice.dto.response;

import com.pda.userservice.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StocksResponseDTO {
    private int stockCnt;
    private List<MyStockWatchlistResponseDTO> stockInfos;
}
