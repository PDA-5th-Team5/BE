package com.pda.stockservice.repository;

import com.pda.stockservice.entity.FavoriteStock;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, Long> {
    Optional<FavoriteStock> findByUserIdAndStock_StockId(String userId, Short stockId);
    // 중복 체크를 위한 메서드 추가
    boolean existsByUserIdAndStock_StockId(String userId, Short stockId);

    @Query("SELECT s.stock.stockId FROM FavoriteStock s WHERE s.userId = :userId")
    List<Short> findStockIdsByUserId(@Param("userId") String userId);
}
