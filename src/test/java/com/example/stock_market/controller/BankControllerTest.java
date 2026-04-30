package com.example.stock_market.controller;

import com.example.stock_market.dto.StockQuantity;
import com.example.stock_market.service.BankService;
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

@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    @Test
    void getBankState_shouldReturn200WithStocks() throws Exception {
        when(bankService.getState()).thenReturn(List.of(
                new StockQuantity("GOLD", 100),
                new StockQuantity("SILVER", 50)
        ));

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks[0].name").value("GOLD"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(100));
    }

    @Test
    void setBankState_shouldReturn200() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stocks\":[{\"name\":\"GOLD\",\"quantity\":100}]}"))
                .andExpect(status().isOk());

        verify(bankService).setState(anyList());
    }
}
