package com.pda.portfolioservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class SharePortfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharePortfolioId;

    private String title;
    private String description;
    private int loadCount;
    private LocalDateTime createdAt;
    private String userId;

    @OneToMany(mappedBy = "sharePortfolio", cascade = CascadeType.ALL)
    List<SharePortfolioComment> comments = new ArrayList<>();

}
