package com.example.stock_market.service;

import com.example.stock_market.dto.AuditLogDto;
import com.example.stock_market.model.AuditLogEntry;
import com.example.stock_market.model.TransactionType;
import com.example.stock_market.repository.AuditLogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuditLogService {

    private final AuditLogEntryRepository auditLogEntryRepository;

    @Transactional
    public AuditLogEntry log(TransactionType type, String walletId, String stockName) {
        return auditLogEntryRepository.save(new AuditLogEntry(type, walletId, stockName));
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getLog() {
        return auditLogEntryRepository.findAllByOrderByIdAsc().stream()
                .map( al -> new AuditLogDto(al.getType(), al.getWalletId(), al.getStockName())).toList();
    }
}
