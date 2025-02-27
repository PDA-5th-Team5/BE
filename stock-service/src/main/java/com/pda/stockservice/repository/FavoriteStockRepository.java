package com.pda.stockservice.repository;

import com.pda.stockservice.entity.FavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, Long> {
    Optional<FavoriteStock> findByUserIdAndStock_StockId(String userId, Short stockId);
    // 중복 체크를 위한 메서드 추가
    boolean existsByUserIdAndStock_StockId(String userId, Short stockId);
}
