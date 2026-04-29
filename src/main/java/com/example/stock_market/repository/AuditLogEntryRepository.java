package com.example.stock_market.repository;

import com.example.stock_market.model.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogEntryRepository extends JpaRepository<AuditLogEntry, Long> {
    public List<AuditLogEntry> findAllByOrderByIdAsc();
}
