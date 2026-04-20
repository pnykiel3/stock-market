package com.example.stock_market.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "wallet_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletStock {

    @EmbeddedId
    private WalletStockId id;

    private Integer quantity;
}
