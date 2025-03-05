package com.pda.stockservice.repository;

import com.pda.stockservice.entity.StockComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import java.util.List;


public interface StockCommentRepository extends JpaRepository<StockComment, Long > {
    List<StockComment> findByStock_StockIdOrderByCreatedAtDesc(Short stockId);
    Optional<List<StockComment>> findByUserId(String userId);  // userId로 댓글 조회
}

