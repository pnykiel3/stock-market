package com.example.stock_market.repository;

import com.example.stock_market.model.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogEntryRepository extends JpaRepository<AuditLogEntry, UUID> {
    public List<AuditLogEntry> findAllByOrderByIdAsc();
}
