package com.pda.portfolioservice.dto.request;

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