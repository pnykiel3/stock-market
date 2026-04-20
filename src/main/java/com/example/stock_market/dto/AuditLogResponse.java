package com.example.stock_market.dto;

import java.util.List;

public record AuditLogResponse(List<AuditLogDto> log) {
}
