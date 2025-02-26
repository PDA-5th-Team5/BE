package com.pda.portfolioservice.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class SharePortfolioComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_portfolio_id")
    private SharePortfolio sharePortfolio;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
}
