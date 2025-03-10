package com.pda.portfolioservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "portfolio_alerts")
public class PortfolioAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId; // 알림 ID

    @ManyToOne(fetch = FetchType.EAGER)  // 즉시 로딩 적용
    @JoinColumn(name = "my_portfolio_id", nullable = false, referencedColumnName = "myPortfolioId", foreignKey = @ForeignKey(name = "FK_PORTFOLIO_ALERTS_MY_PORTFOLIO"))
    private MyPortfolio myPortfolio; // 포트폴리오 ID

    @Column(nullable = false)
    private String userId; // 유저 ID

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 알림 설정 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}