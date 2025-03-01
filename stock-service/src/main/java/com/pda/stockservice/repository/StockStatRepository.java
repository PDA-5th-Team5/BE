package com.pda.stockservice.repository;

import com.pda.stockservice.entity.StockStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockStatRepository extends JpaRepository<StockStat, Short> {
}
