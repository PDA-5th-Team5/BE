package com.pda.stockservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class StockIndicatorThresholds {
    @Id
    private short id;

    private String indicator;
    private Byte value;
    private Double maxValue;
}
