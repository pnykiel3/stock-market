package com.example.stock_market.controller;

import com.example.stock_market.dto.StockQuantity;
import com.example.stock_market.dto.WalletResponse;
import com.example.stock_market.exception.InsufficientStockException;
import com.example.stock_market.exception.StockNotFoundException;
import com.example.stock_market.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @Test
    void getWallet_shouldReturn200() throws Exception {
        WalletResponse response = new WalletResponse("w1", List.of(
                new StockQuantity("GOLD", 10)
        ));
        when(walletService.getWallet("w1")).thenReturn(response);

        mockMvc.perform(get("/wallets/w1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("w1"))
                .andExpect(jsonPath("$.stocks[0].name").value("GOLD"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(10));
    }

    @Test
    void getStockQuantity_shouldReturnNumber() throws Exception {
        when(walletService.getStockQuantity("w1", "GOLD")).thenReturn(42);

        mockMvc.perform(get("/wallets/w1/stocks/GOLD"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    void tradeBuy_shouldReturn200() throws Exception {
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isOk());

        verify(walletService).buy("w1", "GOLD");
    }

    @Test
    void tradeSell_shouldReturn200() throws Exception {
        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isOk());

        verify(walletService).sell("w1", "GOLD");
    }

    @Test
    void tradeBuy_whenStockNotFound_shouldReturn404() throws Exception {
        doThrow(new StockNotFoundException("BITCOIN"))
                .when(walletService).buy("w1", "BITCOIN");

        mockMvc.perform(post("/wallets/w1/stocks/BITCOIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void tradeBuy_whenNoStockInBank_shouldReturn400() throws Exception {
        doThrow(new InsufficientStockException("No stock"))
                .when(walletService).buy("w1", "GOLD");

        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"buy\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tradeSell_whenStockNotFound_shouldReturn404() throws Exception {
        doThrow(new StockNotFoundException("BITCOIN"))
                .when(walletService).sell("w1", "BITCOIN");

        mockMvc.perform(post("/wallets/w1/stocks/BITCOIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void tradeSell_whenNoStockInWallet_shouldReturn400() throws Exception {
        doThrow(new InsufficientStockException("No stock"))
                .when(walletService).sell("w1", "GOLD");

        mockMvc.perform(post("/wallets/w1/stocks/GOLD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"sell\"}"))
                .andExpect(status().isBadRequest());
    }
}
