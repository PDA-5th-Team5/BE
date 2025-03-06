package com.pda.portfolioservice.repository;

import com.pda.portfolioservice.entity.SharePortfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharePortfolioRepository extends JpaRepository<SharePortfolio, Long> {
    // 최신순 정렬 (기본)
    Page<SharePortfolio> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 조회수 높은 순 정렬
    Page<SharePortfolio> findAllByOrderByLoadCountDesc(Pageable pageable);
}
