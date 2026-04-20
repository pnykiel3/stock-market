package com.example.stock_market.dto;

import java.util.List;

public record BankStateRequest(List<StockQuantity> stocks) {
}
