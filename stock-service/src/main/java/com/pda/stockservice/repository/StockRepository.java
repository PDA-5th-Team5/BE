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
    @Query("SELECT s FROM Stock s WHERE s.sector = :sector ORDER BY s.marketCap DESC LIMIT 6")
    List<Stock> findTopCompetitors(@Param("sector") String sector);



    //Optional<Stock> findByTicker(String ticker);
    List<Stock> findByTickerContainingOrCompanyNameContaining(String ticker, String companyName);

;
}
