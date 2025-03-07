package com.pda.portfolioservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TopPortfolioInfoResponseDTO {
    private Long sharePortfolioId;
    private int loadCount;
    private LocalDateTime createdAt;

}
