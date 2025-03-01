package com.pda.portfolioservice.repository;

import com.pda.portfolioservice.entity.MyPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyPortfolioRepository extends JpaRepository<MyPortfolio, Long> {
    List<MyPortfolio> findByUserId(String userId);
}
