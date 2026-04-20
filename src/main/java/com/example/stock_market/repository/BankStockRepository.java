package com.example.stock_market.repository;

import com.example.stock_market.model.BankStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankStockRepository extends JpaRepository<BankStock, String> {
}
