package com.pda.portfolioservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class MyPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myPortfolioId;

    private String title;
    private String description;

    @CreatedDate
    private LocalDateTime createdAt;
    private String userId;
}
