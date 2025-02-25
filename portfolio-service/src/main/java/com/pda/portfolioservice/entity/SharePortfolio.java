package com.pda.portfolioservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class SharePortfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharePortfolioId;

    private String title;
    private String description;
    private Integer loadCount;
    private LocalDateTime createdAt;
    private String userId;
}
