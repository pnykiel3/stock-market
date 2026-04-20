package com.example.stock_market.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletStockId implements Serializable {

    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "stock_name")
    private String stockName;
}
