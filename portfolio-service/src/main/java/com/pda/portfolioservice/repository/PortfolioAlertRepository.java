package com.pda.portfolioservice.repository;

import com.pda.portfolioservice.entity.PortfolioAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioAlertRepository extends JpaRepository<PortfolioAlert, Long> {
    List<PortfolioAlert> findByUserId(String userId);
    void deleteByUserIdAndAlertId(String userId, Long alertId);}