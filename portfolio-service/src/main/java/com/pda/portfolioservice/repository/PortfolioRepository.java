package com.pda.portfolioservice.repository;
import com.pda.portfolioservice.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PortfolioRepository extends MongoRepository<Portfolio, String> {
    Optional<Portfolio> findByCategoryAndPortfolioId(String category, Long portfolioId);
    Optional<Portfolio> findByPortfolioId(Long portfolioId);

    void deleteByPortfolioId(Long portfolioId);
    void deleteByCategoryAndPortfolioId(String category, Long portfolioId);
}