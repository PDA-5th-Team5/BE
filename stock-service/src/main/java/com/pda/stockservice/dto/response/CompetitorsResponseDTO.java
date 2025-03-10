package com.pda.stockservice.dto.response;

import com.pda.stockservice.entity.StockStat;
import com.pda.stockservice.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.pda.stockservice.entity.Stock;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitorsResponseDTO {
    private List<CompetitorsDTO> competitors;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitorsDTO{
        private short stockId;
        private String companyName;
        private String ticker;
        private SnowflakeS snowflakeS;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SnowflakeS {
        private Byte per;
        private Byte lbltRate;
        private Byte marketCap;
        private Byte divYield;
        private Byte foreignerRatio;
    }

    public static CompetitorsResponseDTO toDTO(List<StockStat> stockStatList, List<Short> orderedStockIds) {
        // stockStatList를 stockId를 키로 하는 Map으로 변환
        Map<Short, StockStat> statMap = stockStatList.stream()
                .collect(Collectors.toMap(StockStat::getStockId, stat -> stat));

        // orderedStockIds 순서대로 DTO 생성
        List<CompetitorsDTO> orderedDtoList = orderedStockIds.stream()
                .filter(statMap::containsKey)
                .map(id -> {
                    StockStat ss = statMap.get(id);
                    return CompetitorsDTO.builder()
                            .stockId(ss.getStockId())
                            .companyName(ss.getCompanyName())
                            .ticker(ss.getTicker())
                            .snowflakeS(SnowflakeS.builder()
                                    .per(ss.getPer())
                                    .lbltRate(ss.getLbltRate())
                                    .marketCap(ss.getMarketCap())
                                    .divYield(ss.getDividendYield())
                                    .foreignerRatio(ss.getForeignerRatio())
                                    .build()
                            )
                            .build();
                })
                .collect(Collectors.toList());

        return CompetitorsResponseDTO.builder()
                .competitors(orderedDtoList)
                .build();
    }

    public static String determineSector(Short stockId, StockRepository stockRepository) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found"));
        return stock.getSector();
    }
}
