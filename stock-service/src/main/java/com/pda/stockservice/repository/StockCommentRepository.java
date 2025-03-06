package com.pda.stockservice.repository;

import com.pda.stockservice.entity.StockComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.List;


public interface StockCommentRepository extends JpaRepository<StockComment, Long > {
    @Query("SELECT sc FROM StockComment sc WHERE sc.stock.stockId = :stockId ORDER BY sc.createdAt DESC")
    List<StockComment> findCommentsByStockId(@Param("stockId") Short stockId);

    Optional<List<StockComment>> findByUserId(String userId);  // userId로 댓글 조회
}

