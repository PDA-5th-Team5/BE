package com.pda.portfolioservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class SharePortfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharePortfolioId;

    private String title;
    private String description;
    private int loadCount;

    @CreatedDate
    private LocalDateTime createdAt;
    private String userId;

    @OneToMany(mappedBy = "sharePortfolio", cascade = CascadeType.ALL)
    List<SharePortfolioComment> comments = new ArrayList<>();

}
