package com.pda.portfolioservice.repository;

import com.pda.portfolioservice.entity.SharePortfolioComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharePortfolioCommentRepository extends JpaRepository<SharePortfolioComment, Long> {
    List<SharePortfolioComment> findBysharePortfolio_SharePortfolioId(Long sharePortfolioId);
}
