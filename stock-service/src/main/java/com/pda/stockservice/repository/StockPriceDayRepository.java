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

    @Query("""
    SELECT spd FROM StockPriceDay spd
    WHERE spd.id.stockId IN :stockIds
    AND spd.id.date BETWEEN :startDate AND :endDate
    ORDER BY spd.id.stockId, spd.id.date
""")
    List<StockPriceDay> findStockListClosePricesInRange(
            @Param("stockIds") List<Short> stockIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
    SELECT spd.stock_id, spd.date, spd.close_price 
    FROM stock_price_day spd
    WHERE spd.stock_id IN (:stockIds)
    AND spd.date >= :startDate AND spd.date <= :endDate
    """, nativeQuery = true)
    List<Object[]> findStockPricesNative(
            @Param("stockIds") List<Short> stockIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
