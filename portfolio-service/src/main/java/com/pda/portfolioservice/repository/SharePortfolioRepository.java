package com.pda.portfolioservice.repository;

import com.pda.portfolioservice.entity.SharePortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharePortfolioRepository extends JpaRepository<SharePortfolio, Long> {
}
