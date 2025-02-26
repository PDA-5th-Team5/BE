package com.pda.stockservice.repository;

import com.pda.stockservice.entity.MarketIndicatorPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketIndicatorPriceRepository extends JpaRepository<MarketIndicatorPrice,Long > {
}
