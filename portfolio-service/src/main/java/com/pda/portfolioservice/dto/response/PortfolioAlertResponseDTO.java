package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.entity.PortfolioAlert;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PortfolioAlertResponseDTO {
    private Long alertId;
    private Long portfolioId;
    private String userId;
    private LocalDateTime createdAt;

    public static PortfolioAlertResponseDTO fromEntity(PortfolioAlert alert) {
        return PortfolioAlertResponseDTO.builder()
                .alertId(alert.getAlertId())
                .portfolioId(alert.getMyPortfolio().getMyPortfolioId())
                .userId(alert.getUserId())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}