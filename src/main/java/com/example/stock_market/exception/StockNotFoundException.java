package com.example.stock_market.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String stockName) {

        super("Stock not found: " + stockName);
    }
}
