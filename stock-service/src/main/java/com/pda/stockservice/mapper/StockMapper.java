package com.pda.stockservice.mapper;

import com.pda.stockservice.dto.request.StockFilter;
import com.pda.stockservice.dto.response.StockResponseDTO;
import com.pda.stockservice.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockMapper {
    List<Integer> searchStockStatIds(@Param("market") String market,
                                     @Param("sectors") List<String> sectors,
                                     @Param("filters") StockFilter filters);
    List<StockResponseDTO> findStocksByIds(@Param("stockIds") List<Integer> stockIds);

}