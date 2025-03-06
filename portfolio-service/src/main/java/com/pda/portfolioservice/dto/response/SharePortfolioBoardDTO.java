package com.pda.portfolioservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pda.portfolioservice.dto.request.PortfolioRequestDTO;
import com.pda.portfolioservice.entity.SharePortfolioComment;
import com.pda.portfolioservice.model.Portfolio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class SharePortfolioBoardDTO {
    private Long sharePortfolioId;
    private int loadCount;
    private LocalDateTime createdAt;
    private Portfolio portfolio;
}
