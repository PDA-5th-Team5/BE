package com.pda.stockservice.repository;

import com.pda.stockservice.entity.StockComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockCommentRepository extends JpaRepository<StockComment, Long > {
}
