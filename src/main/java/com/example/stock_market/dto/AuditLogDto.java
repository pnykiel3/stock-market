package com.example.stock_market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuditLogDto(String type,
                          @JsonProperty("wallet_id") String walletId,
                          @JsonProperty("stock_name") String stockName) {
}
