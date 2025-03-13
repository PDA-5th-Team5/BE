package com.pda.portfolioservice.repository;

import com.pda.portfolioservice.entity.SharePortfolioComment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharePortfolioCommentRepository extends JpaRepository<SharePortfolioComment, Long> {
    List<SharePortfolioComment> findBysharePortfolio_SharePortfolioId(Long sharePortfolioId, Sort sort);

    Optional<List<SharePortfolioComment>> findByUserId(String userId);  // userId로 댓글 조회
}
