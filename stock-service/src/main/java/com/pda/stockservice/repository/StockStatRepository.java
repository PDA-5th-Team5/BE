package com.pda.stockservice.repository;

import com.pda.stockservice.entity.StockStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockStatRepository extends JpaRepository<StockStat, Short> {
    List<StockStat> findByStockIdIn(List<Short> stockIds);
}
