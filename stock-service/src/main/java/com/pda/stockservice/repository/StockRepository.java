package com.pda.stockservice.repository;

import com.pda.stockservice.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Short> {
    //경쟁사 뽑아내기
    List<Stock> findTop6BySectorOrderByMarketCapDesc(String sector);
}
