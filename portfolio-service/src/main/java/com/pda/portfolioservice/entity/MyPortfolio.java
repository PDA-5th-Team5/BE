package com.pda.portfolioservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
public class MyPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myPortfolioId;

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private String userId;
}
