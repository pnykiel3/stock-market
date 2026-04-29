package com.example.stock_market.repository;

import com.example.stock_market.model.BankStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankStockRepository extends JpaRepository<BankStock, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankStock b WHERE b.name = :name")
    Optional<BankStock> findByIdForUpdate(@Param("name") String name);
}
