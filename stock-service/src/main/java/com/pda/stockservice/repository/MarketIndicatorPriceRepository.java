package com.pda.stockservice.repository;

import com.pda.stockservice.entity.MarketIndicatorPrice;
import com.pda.stockservice.enums.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarketIndicatorPriceRepository extends JpaRepository<MarketIndicatorPrice,Long > {

    @Query("SELECT m FROM MarketIndicatorPrice m WHERE m.id.market = :market AND m.id.date BETWEEN :startDate AND :endDate")
    List<MarketIndicatorPrice> findMarketPricesRange(
            @Param("market") Market market,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
