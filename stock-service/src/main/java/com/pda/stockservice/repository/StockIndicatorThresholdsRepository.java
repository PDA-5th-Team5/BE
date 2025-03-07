package com.pda.stockservice.repository;

import com.pda.stockservice.entity.StockIndicatorThresholds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StockIndicatorThresholdsRepository extends JpaRepository<StockIndicatorThresholds, Short> {

    // 특정 indicator 값에 해당하는 목록 조회 (value 기준 정렬)
    List<StockIndicatorThresholds> findByIndicatorOrderByValueAsc(String indicator);
}
