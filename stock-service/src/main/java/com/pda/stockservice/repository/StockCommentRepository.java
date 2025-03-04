package com.pda.stockservice.repository;

import com.pda.stockservice.entity.StockComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockCommentRepository extends JpaRepository<StockComment, Long > {
//    List<StockComment> findByUserId(String userId);  // userId로 댓글 조회
    Optional<List<StockComment>> findByUserId(String userId);  // userId로 댓글 조회
}
