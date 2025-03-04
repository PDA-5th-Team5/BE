package com.pda.stockservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockFilterRequest {
    private String market;
    private List<String> sectors;
    private StockFilter filters;
}