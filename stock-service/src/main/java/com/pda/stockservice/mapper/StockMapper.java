package com.pda.stockservice.mapper;

import com.pda.stockservice.dto.request.SnowflakeDTO;
import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.response.StockResponseDTO;
import com.pda.stockservice.entity.Stock;
import com.pda.stockservice.enums.Market;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockMapper {
    List<SnowflakeDTO> searchStockStatIds(@Param("marketType") List<Market> marketType,
                                          @Param("sector") List<String> sector,
                                          @Param("filters") StockFilter filters,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);

    List<StockResponseDTO> findStocksByIds(@Param("stockIds") List<Integer> stockIds);
}