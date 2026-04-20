package com.example.stock_market.repository;

import com.example.stock_market.model.WalletStock;
import com.example.stock_market.model.WalletStockId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletStockRepository extends JpaRepository<WalletStock, WalletStockId> {

    List<WalletStock> findByIdWalletId(String walletId);
    Optional<WalletStock> findByIdWalletIdAndIdStockName(String walletId, String stockName);
}
