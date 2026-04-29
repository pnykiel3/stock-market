package com.example.stock_market.service;

import com.example.stock_market.dto.StockQuantity;
import com.example.stock_market.exception.InsufficientStockException;
import com.example.stock_market.exception.StockNotFoundException;
import com.example.stock_market.model.BankStock;
import com.example.stock_market.repository.BankStockRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BankService {

    private final BankStockRepository bankStockRepository;

    @Transactional
    public void setState(List<StockQuantity> stockQuantities) {
        bankStockRepository.deleteAll();

        List<BankStock> bankStocks = stockQuantities.stream().map(
                s -> new BankStock(s.name(), s.quantity())
        ).toList();
        bankStockRepository.saveAll(bankStocks);
    }

    @Transactional
    public void buyFromBank(String stockName) {
        BankStock bankStock = bankStockRepository.findByIdForUpdate(stockName)
                .orElseThrow(() -> new StockNotFoundException("Stock has not been found"));
        if (bankStock.getQuantity() <= 0) throw new InsufficientStockException("The stock you wanted to buy is not available");

        bankStock.setQuantity(bankStock.getQuantity() - 1);
        bankStockRepository.save(bankStock);
    }

    @Transactional
    public void sellToBank(String stockName) {
        BankStock bankStock = bankStockRepository.findByIdForUpdate(stockName)
                .orElseThrow(() -> new StockNotFoundException("Stock has not been found"));
        bankStock.setQuantity(bankStock.getQuantity() + 1);
        bankStockRepository.save(bankStock);
    }

    @Transactional(readOnly = true)
    public List<StockQuantity> getState() {
        return bankStockRepository.findAll().stream().map( bs -> new StockQuantity(bs.getName(), bs.getQuantity())).toList();
    }

    @Transactional(readOnly = true)
    public boolean existsStock(String name) {
        return bankStockRepository.findById(name).isPresent();
    }

}
