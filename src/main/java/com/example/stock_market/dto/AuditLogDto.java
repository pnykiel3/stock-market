package com.example.stock_market.dto;

import com.example.stock_market.model.TransacionType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuditLogDto(TransacionType type,
                          @JsonProperty("wallet_id") String walletId,
                          @JsonProperty("stock_name") String stockName) {
}
