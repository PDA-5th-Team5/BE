package com.pda.stockservice.repository;

import com.pda.stockservice.entity.FavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;


public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, Long> {
    Optional<FavoriteStock> findByUserIdAndStock_StockId(String userId, Short stockId);
    // 중복 체크를 위한 메서드 추가
    boolean existsByUserIdAndStock_StockId(String userId, Short stockId);

    // 사용자 관심 등록 주식 리스트 조회
    List<FavoriteStock> findByUserId(String userId);
}
