package com.example.stock_market.service;

import com.example.stock_market.dto.AuditLogDto;
import com.example.stock_market.model.AuditLogEntry;
import com.example.stock_market.model.TransactionType;
import com.example.stock_market.repository.AuditLogEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogEntryRepository auditLogEntryRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void log_shouldSaveEntryToRepository() {
        AuditLogEntry savedEntry = new AuditLogEntry(TransactionType.BUY, "wallet1", "GOLD");
        when(auditLogEntryRepository.save(any())).thenReturn(savedEntry);

        AuditLogEntry result = auditLogService.log(TransactionType.BUY, "wallet1", "GOLD");

        assertNotNull(result);
        assertEquals(TransactionType.BUY, result.getType());
        assertEquals("wallet1", result.getWalletId());
        assertEquals("GOLD", result.getStockName());
        verify(auditLogEntryRepository).save(any(AuditLogEntry.class));
    }

    @Test
    void getLog_shouldReturnListOfDtos() {
        List<AuditLogEntry> entries = List.of(
                new AuditLogEntry(1L, TransactionType.BUY, "w1", "GOLD", LocalDateTime.now()),
                new AuditLogEntry(2L, TransactionType.SELL, "w2", "SILVER", LocalDateTime.now())
        );
        when(auditLogEntryRepository.findAllByOrderByIdAsc()).thenReturn(entries);

        List<AuditLogDto> result = auditLogService.getLog();

        assertEquals(2, result.size());
        assertEquals(TransactionType.BUY, result.get(0).type());
        assertEquals("w1", result.get(0).walletId());
        assertEquals("GOLD", result.get(0).stockName());
        assertEquals(TransactionType.SELL, result.get(1).type());
    }

    @Test
    void getLog_whenEmpty_shouldReturnEmptyList() {
        when(auditLogEntryRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

        List<AuditLogDto> result = auditLogService.getLog();

        assertTrue(result.isEmpty());
    }
}
