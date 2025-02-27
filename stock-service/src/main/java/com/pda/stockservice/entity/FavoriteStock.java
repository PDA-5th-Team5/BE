package com.pda.stockservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteStockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock; //Stock 엔티티 객체 전체 참조

    private String userId;

    @Builder
    private FavoriteStock(Stock stock, String userId) {
        this.stock = stock;
        this.userId = userId;
    }
}
