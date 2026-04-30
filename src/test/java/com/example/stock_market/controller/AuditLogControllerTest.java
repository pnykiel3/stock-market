package com.example.stock_market.controller;

import com.example.stock_market.dto.AuditLogDto;
import com.example.stock_market.model.TransactionType;
import com.example.stock_market.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditLogController.class)
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogService auditLogService;

    @Test
    void getLog_shouldReturn200WithLogEntries() throws Exception {
        when(auditLogService.getLog()).thenReturn(List.of(
                new AuditLogDto(TransactionType.BUY, "w1", "GOLD"),
                new AuditLogDto(TransactionType.SELL, "w2", "SILVER")
        ));

        mockMvc.perform(get("/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.log[0].type").value("buy"))
                .andExpect(jsonPath("$.log[0].wallet_id").value("w1"))
                .andExpect(jsonPath("$.log[0].stock_name").value("GOLD"))
                .andExpect(jsonPath("$.log[1].type").value("sell"));
    }

    @Test
    void getLog_whenEmpty_shouldReturnEmptyLog() throws Exception {
        when(auditLogService.getLog()).thenReturn(List.of());

        mockMvc.perform(get("/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.log").isEmpty());
    }
}
