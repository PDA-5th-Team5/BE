package com.pda.stockservice.repository;

import com.pda.stockservice.entity.Stock;
import com.pda.stockservice.entity.StockPriceDay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockPriceDayRepository extends JpaRepository<StockPriceDay, StockPriceDay.StockPriceDayId > {

    @Query(value = "SELECT * FROM stock_price_day spd " +
            "WHERE spd.stock_id = :stockId " +
            "AND spd.date >= DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR) " +
            "AND spd.date <= CURRENT_DATE " +
            "ORDER BY spd.date ASC", nativeQuery = true)
    List<StockPriceDay> findCandleDateByStockId(@Param("stockId") Short stockId);
}
