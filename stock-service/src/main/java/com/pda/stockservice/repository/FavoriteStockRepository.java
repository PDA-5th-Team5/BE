package com.pda.stockservice.repository;

import com.pda.stockservice.entity.FavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, Long> {
}
