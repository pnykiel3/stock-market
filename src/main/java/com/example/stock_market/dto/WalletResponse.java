package com.example.stock_market.dto;

import java.util.List;

public record WalletResponse(String id, List<StockQuantity> stocks) {
}
