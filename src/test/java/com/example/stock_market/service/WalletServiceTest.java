package com.example.stock_market.service;

import com.example.stock_market.dto.WalletResponse;
import com.example.stock_market.exception.InsufficientStockException;
import com.example.stock_market.exception.StockNotFoundException;
import com.example.stock_market.model.TransactionType;
import com.example.stock_market.model.WalletStock;
import com.example.stock_market.model.WalletStockId;
import com.example.stock_market.repository.WalletStockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletStockRepository walletStockRepository;

    @Mock
    private BankService bankService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private WalletService walletService;

    @Test
    void buy_newStock_shouldCreateWalletStockWithQuantityOne() {
        when(walletStockRepository.findByIdWalletIdAndIdStockName("wallet1", "GOLD"))
                .thenReturn(Optional.empty());

        walletService.buy("wallet1", "GOLD");

        verify(bankService).buyFromBank("GOLD");
        verify(walletStockRepository).save(any(WalletStock.class));
        verify(auditLogService).log(TransactionType.BUY, "wallet1", "GOLD");
    }

    @Test
    void buy_existingStock_shouldIncrementQuantity() {
        WalletStock existing = new WalletStock(new WalletStockId("wallet1", "GOLD"), 5);
        when(walletStockRepository.findByIdWalletIdAndIdStockName("wallet1", "GOLD"))
                .thenReturn(Optional.of(existing));

        walletService.buy("wallet1", "GOLD");

        assertEquals(6, existing.getQuantity());
        verify(bankService).buyFromBank("GOLD");
        verify(walletStockRepository).save(existing);
        verify(auditLogService).log(TransactionType.BUY, "wallet1", "GOLD");
    }

    @Test
    void buy_whenStockNotInBank_shouldThrowStockNotFoundException() {
        doThrow(new StockNotFoundException("BITCOIN"))
                .when(bankService).buyFromBank("BITCOIN");

        assertThrows(StockNotFoundException.class, () -> walletService.buy("wallet1", "BITCOIN"));

        verify(walletStockRepository, never()).save(any());
    }

    @Test
    void buy_whenBankHasNoQuantity_shouldThrowInsufficientStockException() {
        doThrow(new InsufficientStockException("No stock"))
                .when(bankService).buyFromBank("GOLD");

        assertThrows(InsufficientStockException.class, () -> walletService.buy("wallet1", "GOLD"));

        verify(walletStockRepository, never()).save(any());
    }

    @Test
    void sell_shouldDecrementQuantityAndSellToBank() {
        when(bankService.existsStock("GOLD")).thenReturn(true);
        WalletStock ws = new WalletStock(new WalletStockId("wallet1", "GOLD"), 5);
        when(walletStockRepository.findByIdWalletIdAndIdStockName("wallet1", "GOLD"))
                .thenReturn(Optional.of(ws));

        walletService.sell("wallet1", "GOLD");

        assertEquals(4, ws.getQuantity());
        verify(walletStockRepository).save(ws);
        verify(bankService).sellToBank("GOLD");
        verify(auditLogService).log(TransactionType.SELL, "wallet1", "GOLD");
    }

    @Test
    void sell_whenStockNotInBank_shouldThrowStockNotFoundException() {
        when(bankService.existsStock("BITCOIN")).thenReturn(false);

        assertThrows(StockNotFoundException.class, () -> walletService.sell("wallet1", "BITCOIN"));
    }

    @Test
    void sell_whenStockNotInWallet_shouldThrowInsufficientStockException() {
        when(bankService.existsStock("GOLD")).thenReturn(true);
        when(walletStockRepository.findByIdWalletIdAndIdStockName("wallet1", "GOLD"))
                .thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class, () -> walletService.sell("wallet1", "GOLD"));
    }

    @Test
    void sell_whenQuantityIsZero_shouldThrowInsufficientStockException() {
        when(bankService.existsStock("GOLD")).thenReturn(true);
        WalletStock ws = new WalletStock(new WalletStockId("wallet1", "GOLD"), 0);
        when(walletStockRepository.findByIdWalletIdAndIdStockName("wallet1", "GOLD"))
                .thenReturn(Optional.of(ws));

        assertThrows(InsufficientStockException.class, () -> walletService.sell("wallet1", "GOLD"));
    }

    @Test
    void getWallet_shouldReturnWalletWithStocks() {
        when(walletStockRepository.findByIdWalletId("wallet1")).thenReturn(List.of(
                new WalletStock(new WalletStockId("wallet1", "GOLD"), 10),
                new WalletStock(new WalletStockId("wallet1", "SILVER"), 5)
        ));

        WalletResponse response = walletService.getWallet("wallet1");

        assertEquals("wallet1", response.id());
        assertEquals(2, response.stocks().size());
        assertEquals("GOLD", response.stocks().get(0).name());
        assertEquals(10, response.stocks().get(0).quantity());
    }

    @Test
    void getWallet_whenNotExists_shouldReturnEmptyStocks() {
        when(walletStockRepository.findByIdWalletId("unknown")).thenReturn(List.of());

        WalletResponse response = walletService.getWallet("unknown");

        assertEquals("unknown", response.id());
        assertTrue(response.stocks().isEmpty());
    }

    @Test
    void getStockQuantity_shouldReturnQuantityWhenExists() {
        WalletStock ws = new WalletStock(new WalletStockId("wallet1", "GOLD"), 42);
        when(walletStockRepository.findByIdWalletIdAndIdStockName("wallet1", "GOLD"))
                .thenReturn(Optional.of(ws));

        int result = walletService.getStockQuantity("wallet1", "GOLD");

        assertEquals(42, result);
    }

    @Test
    void getStockQuantity_shouldReturnZeroWhenNotExists() {
        when(walletStockRepository.findByIdWalletIdAndIdStockName("wallet1", "BITCOIN"))
                .thenReturn(Optional.empty());

        int result = walletService.getStockQuantity("wallet1", "BITCOIN");

        assertEquals(0, result);
    }
}
