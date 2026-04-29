package com.example.stock_market.service;

import com.example.stock_market.dto.StockQuantity;
import com.example.stock_market.dto.WalletResponse;
import com.example.stock_market.exception.InsufficientStockException;
import com.example.stock_market.exception.StockNotFoundException;
import com.example.stock_market.model.TransactionType;
import com.example.stock_market.model.WalletStock;
import com.example.stock_market.model.WalletStockId;
import com.example.stock_market.repository.WalletStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletStockRepository walletStockRepository;
    private final BankService bankService;
    private final AuditLogService auditLogService;

    @Transactional
    public void buy( String walletId, String stockName) {
        bankService.buyFromBank(stockName);
        WalletStock walletStock = walletStockRepository.findByIdWalletIdAndIdStockName(walletId, stockName)
                .orElseGet( () -> new WalletStock(new WalletStockId(walletId, stockName), 0));

        walletStock.setQuantity(walletStock.getQuantity()+1);
        walletStockRepository.save(walletStock);
        auditLogService.log(TransactionType.BUY, walletId, stockName);
    }

    @Transactional
    public void sell( String walletId, String stockName) {
        if (!bankService.existsStock(stockName)) {
            throw new StockNotFoundException(stockName);
        }
        WalletStock walletStock = walletStockRepository.findByIdWalletIdAndIdStockName(walletId, stockName)
                .orElseThrow(() -> new InsufficientStockException("No stock in wallet: " + stockName));

        if (walletStock.getQuantity() <= 0) {
            throw new InsufficientStockException("No stock in wallet: " + stockName);
        }

        walletStock.setQuantity(walletStock.getQuantity()-1);
        walletStockRepository.save(walletStock);
        bankService.sellToBank(stockName);
        auditLogService.log(TransactionType.SELL, walletId, stockName);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(String walletId) {
        List<StockQuantity> stocks = walletStockRepository.findByIdWalletId(walletId).stream()
                .map( w -> new StockQuantity(w.getId().getStockName(), w.getQuantity())).toList();
        return new WalletResponse(walletId, stocks);
    }

    @Transactional(readOnly = true)
    public Integer getStockQuantity(String walletId, String stockName){
        return walletStockRepository.findByIdWalletIdAndIdStockName(walletId, stockName)
                .map(WalletStock::getQuantity).orElse(0);
    }

}
