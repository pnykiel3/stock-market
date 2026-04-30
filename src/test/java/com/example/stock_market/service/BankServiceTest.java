package com.example.stock_market.service;

import com.example.stock_market.dto.StockQuantity;
import com.example.stock_market.exception.InsufficientStockException;
import com.example.stock_market.exception.StockNotFoundException;
import com.example.stock_market.model.BankStock;
import com.example.stock_market.repository.BankStockRepository;
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
class BankServiceTest {

    @Mock
    private BankStockRepository bankStockRepository;

    @InjectMocks
    private BankService bankService;

    @Test
    void setState_shouldDeleteAllAndSaveNewStocks() {
        List<StockQuantity> stocks = List.of(
                new StockQuantity("GOLD", 100),
                new StockQuantity("SILVER", 50)
        );

        bankService.setState(stocks);

        verify(bankStockRepository).deleteAll();
        verify(bankStockRepository).saveAll(anyList());
    }

    @Test
    void getState_shouldReturnAllStocks() {
        when(bankStockRepository.findAll()).thenReturn(List.of(
                new BankStock("GOLD", 100),
                new BankStock("SILVER", 50)
        ));

        List<StockQuantity> result = bankService.getState();

        assertEquals(2, result.size());
        assertEquals("GOLD", result.get(0).name());
        assertEquals(100, result.get(0).quantity());
    }

    @Test
    void getState_whenEmpty_shouldReturnEmptyList() {
        when(bankStockRepository.findAll()).thenReturn(List.of());

        List<StockQuantity> result = bankService.getState();

        assertTrue(result.isEmpty());
    }

    @Test
    void buyFromBank_shouldDecrementQuantity() {
        BankStock stock = new BankStock("GOLD", 10);
        when(bankStockRepository.findByIdForUpdate("GOLD")).thenReturn(Optional.of(stock));

        bankService.buyFromBank("GOLD");

        assertEquals(9, stock.getQuantity());
        verify(bankStockRepository).save(stock);
    }

    @Test
    void buyFromBank_whenStockNotFound_shouldThrowException() {
        when(bankStockRepository.findByIdForUpdate("BITCOIN")).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> bankService.buyFromBank("BITCOIN"));
    }

    @Test
    void buyFromBank_whenQuantityIsZero_shouldThrowException() {
        BankStock stock = new BankStock("GOLD", 0);
        when(bankStockRepository.findByIdForUpdate("GOLD")).thenReturn(Optional.of(stock));

        assertThrows(InsufficientStockException.class, () -> bankService.buyFromBank("GOLD"));
    }

    @Test
    void sellToBank_shouldIncrementQuantity() {
        BankStock stock = new BankStock("GOLD", 10);
        when(bankStockRepository.findByIdForUpdate("GOLD")).thenReturn(Optional.of(stock));

        bankService.sellToBank("GOLD");

        assertEquals(11, stock.getQuantity());
        verify(bankStockRepository).save(stock);
    }

    @Test
    void sellToBank_whenStockNotFound_shouldThrowException() {
        when(bankStockRepository.findByIdForUpdate("BITCOIN")).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> bankService.sellToBank("BITCOIN"));
    }

    @Test
    void existsStock_shouldReturnTrueWhenStockExists() {
        when(bankStockRepository.findById("GOLD")).thenReturn(Optional.of(new BankStock("GOLD", 10)));

        assertTrue(bankService.existsStock("GOLD"));
    }

    @Test
    void existsStock_shouldReturnFalseWhenStockDoesNotExist() {
        when(bankStockRepository.findById("BITCOIN")).thenReturn(Optional.empty());

        assertFalse(bankService.existsStock("BITCOIN"));
    }
}
