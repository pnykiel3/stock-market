package com.example.stock_market.dto;

import com.example.stock_market.model.TransacionType;
import jakarta.validation.constraints.NotNull;

public record TradeRequest(@NotNull TransacionType type) {
}
