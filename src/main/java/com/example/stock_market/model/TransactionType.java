package com.example.stock_market.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionType {
    @JsonProperty("buy") BUY,
    @JsonProperty("sell") SELL
}