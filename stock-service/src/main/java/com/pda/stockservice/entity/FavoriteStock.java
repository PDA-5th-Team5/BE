package com.pda.stockservice.entity;

import jakarta.persistence.*;

@Entity
public class FavoriteStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteStockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock; //Stock 엔티티 객체 전체 참조

    private String userId;

}
