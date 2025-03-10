package com.pda.stockservice.repository;

import com.pda.stockservice.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Short> {
    @Query("SELECT s FROM Stock s WHERE s.sector = (SELECT s2.sector FROM Stock s2 WHERE s2.stockId = :stockId) ORDER BY s.marketCap DESC")
    List<Stock> findTopCompetitors(@Param("stockId") Short stockId);



    //Optional<Stock> findByTicker(String ticker);
    List<Stock> findByTickerContainingOrCompanyNameContaining(String ticker, String companyName);

    @Query("SELECT DISTINCT s.sector FROM Stock s WHERE s.sector IS NOT NULL")
    List<String> findDistinctSectors();

}
