package com.pda.stockservice.repository;

import com.pda.stockservice.entity.MarketIndicatorPrice;
import com.pda.stockservice.entity.StockPriceDay;

import com.pda.stockservice.enums.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockPriceDayRepository extends JpaRepository<StockPriceDay, StockPriceDay.StockPriceDayId > {

    @Query(value = "SELECT * FROM stock_price_day spd " +
            "WHERE spd.stock_id = :stockId " +
            "AND spd.date >= DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR) " +
            "AND spd.date <= CURRENT_DATE " +
            "ORDER BY spd.date ASC", nativeQuery = true)
    List<StockPriceDay> findCandleDateByStockId(@Param("stockId") Short stockId);

    @Query("""
    SELECT s FROM StockPriceDay s
    WHERE s.id.stockId = :stockId AND s.id.date BETWEEN :startDate AND :endDate
    ORDER BY s.id.date
""")
    List<StockPriceDay> findStockClosePricesInRange(
            @Param("stockId") Short stockId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
