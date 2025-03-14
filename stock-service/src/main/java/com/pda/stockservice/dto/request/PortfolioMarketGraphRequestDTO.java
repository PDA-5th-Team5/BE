package com.pda.stockservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioMarketGraphRequestDTO {

    private List<Short> stockIds;


    @Override
    public String toString() {
        return stockIds == null ? "[]" :
                stockIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioMarketGraphRequestDTO that = (PortfolioMarketGraphRequestDTO) o;
        return Objects.equals(stockIds, that.stockIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockIds);
    }
}
