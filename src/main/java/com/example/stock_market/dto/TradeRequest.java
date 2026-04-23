package com.example.stock_market.dto;

import com.example.stock_market.model.TransactionType;
import jakarta.validation.constraints.NotNull;

public record TradeRequest(@NotNull TransactionType type) {
}
