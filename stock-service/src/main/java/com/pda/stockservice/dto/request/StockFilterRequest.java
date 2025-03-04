package com.pda.stockservice.dto.request;

import com.pda.stockservice.enums.Market;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockFilterRequest {
    private String marketType;
    private List<String> sector;
    private StockFilter filters;
}